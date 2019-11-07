/***
 * Copyright 2019 icefrog.su
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icefrog.async.export.component.thread;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.icefrog.async.export.component.ApiCacheQueue;
import com.icefrog.async.export.component.OssComponent;
import com.icefrog.async.export.dal.entity.SysExportConf;
import com.icefrog.async.export.dal.entity.SysExportPlan;
import com.icefrog.async.export.dal.mapper.SysExportConfMapper;
import com.icefrog.async.export.dal.mapper.SysExportPlanMapper;
import com.icefrog.async.export.dto.ExportApiReqDto;
import com.icefrog.async.export.dto.IOResultDto;
import com.icefrog.async.export.integration.annotation.DictionaryScan;
import com.icefrog.async.export.integration.enums.PlanStatus;
import com.icefrog.async.export.integration.export.BaseResultSet;
import com.icefrog.async.export.integration.export.IExport;
import com.icefrog.async.export.integration.spring.ApplicationContextBeanProvider;
import com.icefrog.async.export.redis.JedisService;
import com.icefrog.async.export.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/***
 * 导出计划异步消费线程. 该线程将不断阻塞take队列中的结果。消费信息并上传oss等.
 *
 * @author icefrog
 */
@Slf4j
@Component
public class QueueConsumerRunnable implements Runnable {

    @Resource
    private SysExportConfMapper sysExportConfMapper;

    @Resource
    private SysExportPlanMapper sysExportPlanMapper;

    @Resource
    private JedisService jedisService;

    @Resource
    private OssComponent ossComponent;

    @Resource
    private ApplicationContextBeanProvider applicationContextBeanProvider;

    /***
     * 注入可配置的当列为null时的默认替换字符. 不配置择默认为空字符串
     */
    @Value("${export.defaultNullStr:}")
    private String defaultNullChar;

    /***
     * 导出文件磁盘临时目录
     */
    @Value("${export.temp.dir}")
    private String tempDir;

    /***
     * 导出excel sheetName
     */
    @Value("${export.sheetName:Sheet1}")
    private String sheetName;

    /***
     * 默认excel文件后缀
     */
    @Value("${export.fileSuffix:xlsx}")
    private String fileSuffix;

    /***
     * oss存储目录前缀. 默认：export/download
     */
    @Value("${export.prefixUrl:export/download}")
    private String prefixUrl;

    /***
     * 国际化默认编码标记
     */
    @Value("${i18n.default.mark}")
    private String i18nDefaultMark;

    @Override
    public void run() {

        if(log.isInfoEnabled()) {
            log.info("{}成功分配资源, 监听队列...", QueueConsumerRunnable.class.getSimpleName());
        }

        while(true) {

            // 声明导出最终内存结果集
            List<Map<String, String>> resultSet = Lists.newArrayList();

            // 声明当前处理的导出column配置
            Map<String, Object> columnConf = Maps.newHashMap();

            // 声明待处理的导出计划
            ExportApiReqDto plan = null;

            // 细粒度异常处理, 避免单次队列阻塞获取异常导致整个线程不可用
            try {
                plan = ApiCacheQueue.take();

                String beanId = plan.getBeanId();
                if(StringUtils.isBlank(beanId)) {
                    log.error("错误的尝试处理一次请求作业. beanId:{}, methodName:{}, userId:{}, requestParams:{}",
                            plan.getBeanId(), plan.getMethodName(), plan.getUserId(),
                            plan.getRequestParams());
                    break;
                }

                IExport bean = applicationContextBeanProvider.getBean(plan.getBeanId(), IExport.class);
                List<BaseResultSet> baseResultSets = bean.exportHandler(null);

                // 通过beanId查询column配置
                SysExportConf sysExportConf = sysExportConfMapper.queryColumnConfWithBeanId(plan.getBeanId());
                if(sysExportConf == null) {
                    log.error("beanId:{}无法从数据库检索到对应的column conf, 丢弃该作业计划!");
                    break;
                }

                String columnConfJson = sysExportConf.getColumnConfJson();
                if(StringUtils.isBlank(columnConfJson)) {
                    log.error("beanId:{} 的 column config json未配置, 丢弃该作业计划!");
                    break;
                }
                // JSON to Map. key: java property. value: description
                columnConf = JSON.parseObject(columnConfJson).getInnerMap();

                Set<String> keys = columnConf.keySet();
                Iterator<String> columnConfIterator = keys.iterator();
                // 反射解析返回结果
                for (BaseResultSet baseResultSet : baseResultSets) {
                    Map<String, String> item = Maps.newHashMap();

                    while(columnConfIterator.hasNext()) {
                        // column配置中的java属性.
                        String property = columnConfIterator.next();
                        Field field = ReflectUtil.getField(baseResultSet.getClass(), property);
                        // 设置反射私有对象可访问
                        ReflectUtil.setAccessible(field);

                        // 反射获取与封装对象属性值
                        Object propertyVal = field.get(baseResultSet) == null ? defaultNullChar : field.get(baseResultSet);

                        // 扫描注解信息
                        DictionaryScan annotation = field.getAnnotation(DictionaryScan.class);
                        if(annotation != null && annotation.scan()) {
                            // 存在字典扫描
                            String code = annotation.code();
                            if(StringUtils.isNotBlank(code)) {
                                String i18nConfJson = jedisService.init().hget("dictItemCache", code + "_" + propertyVal);
                                if (StringUtils.isNotBlank(i18nConfJson)) {
                                    Map<String, Object> i18nConfMap = JSON.parseObject(i18nConfJson).getInnerMap();
                                    Object i18nVal = i18nConfMap.get(plan.getI18n());
                                    if (i18nVal == null) {
                                        // 获取默认国际化配置再度获取一次
                                        i18nVal = i18nConfMap.get(i18nDefaultMark);
                                        if (i18nVal != null) {
                                            // 替换原字典值
                                            propertyVal = i18nVal;
                                        }
                                    } else {
                                        // 替换原字典值
                                        propertyVal = i18nVal;
                                    }
                                }
                            }
                        }
                        item.put(property, String.valueOf(propertyVal));
                    }
                    // 重置迭代器游标变量
                    columnConfIterator = keys.iterator();
                    // 添加该对象到结果集
                    resultSet.add(item);
                }

                // 1. 将文件写入临时磁盘区域
                IOResultDto ioResultDto = writeFile(resultSet, columnConf);

                // 2. 将文件上传到OSS
                String url = uploadToOss(ioResultDto.getAbsolutePath(), ioResultDto.getFileName());

                // 3. 从磁盘上移除临时文件
                removeFile(ioResultDto.getAbsolutePath());

                // 回写数据库(成功状态)
                rewritePlan(plan.getPlanId(), Long.valueOf(resultSet.size()), url, PlanStatus.SUCCESS.getValue(), 0, plan.getI18n(), null);

            } catch (Exception e) {
                log.error("队列处理异常(异步消费队列), 异常信息:" + e.getMessage(), e);
                // 回写数据库(失败状态)
                rewritePlan(plan.getPlanId(), Long.valueOf(resultSet.size()), null, PlanStatus.FAILED.getValue(), 0, plan.getI18n(), e.getMessage());
            }
        }
    }


    /***
     * 写出内存数据到磁盘。（取决于配置的IO临时存储目录）
     *
     * @param resultSet 待写入磁盘的内存结果集
     * @param columnConf column配置map
     * @throws IOException 可能的IO异常。 该实现不做处理
     * @return IOResultDto 写入成功后的文件信息
     */
    private IOResultDto writeFile(List<Map<String, String>> resultSet, Map<String, Object> columnConf) throws IOException {

        IOResultDto result = new IOResultDto();

        // 1. 将map结构列配置信息转为list结构
        List<List<String>> headers = new ArrayList<>(columnConf.size());
        for (Object value : columnConf.values()) {
            List<String> headItem = Lists.newArrayList();
            headItem.add(String.valueOf(value));
            headers.add(headItem);
        }

        // 2. 将map结构的数据主体信息转换为list结构
        List<List<String>> dataList = new ArrayList<>(resultSet.size());
        for (Map<String, String> cycleItem : resultSet) {
            Collection<String> values = cycleItem.values();
            List<String> dataItem = new ArrayList<>(values);
            dataList.add(dataItem);
        }

        // 3. 构建文件名与文件临时目录
        String fileName = IdUtil.fastSimpleUUID() + "." + fileSuffix;
        String absolutePath = tempDir + File.separator + fileName;

        // 写出到本地磁盘
        ExcelUtil.dynamicHeadWrite(absolutePath, sheetName, headers, dataList);

        // 回写文件描述信息
        result.setDir(tempDir);
        result.setFileName(fileName);
        result.setAbsolutePath(absolutePath);
        return result;
    }

    /***
     * 上传文件到oss服务器
     *
     * @param absolutePath 文件本地绝对路径。 含文件名称以及后缀
     * @param fileName 文件名称。 含后缀
     * @return 可访问的资源地址（http url）
     */
    private String uploadToOss(String absolutePath, String fileName) {

        return ossComponent.putObject(prefixUrl, fileName, absolutePath);
    }

    /***
     * 删除指定绝对路径所指向的文件。
     *
     * @see FileUtils#forceDelete(File)
     * @param absolutePath 文件绝对路径
     */
    private void removeFile(String absolutePath) {
        try {

            FileUtils.forceDelete(new File(absolutePath));

        } catch (IOException e) {
            log.error("移除路径：{} 下的文件异常: {}", absolutePath, e.getMessage(), e);
        }
    }

    /***
     * 更新plan记录状态
     *
     * @param planId 待更新的planId
     * @param dataLine 字段[data_line]
     * @param ossUrl 字段[url]
     * @param status 字段[plan_status]
     * @param retryQty 字段[retry_qty]
     * @param i18n 字段[i18n]
     * @return 更新结果
     */
    private boolean rewritePlan(String planId, Long dataLine, String ossUrl, String status, int retryQty, String i18n, String failedMsg) {

        SysExportPlan plan = new SysExportPlan();
        plan.setId(planId);
        plan.setLineCount(dataLine);
        plan.setUrl(ossUrl);
        plan.setPlanStatus(status);
        plan.setRetryQty(retryQty);
        plan.setTmSuccess(new Date());
        plan.setFailedMsg(failedMsg);
        plan.setI18n(i18n);

        // 更新记录状态
        return sysExportPlanMapper.updateByPrimaryKeySelective(plan) > 0;
    }
}
