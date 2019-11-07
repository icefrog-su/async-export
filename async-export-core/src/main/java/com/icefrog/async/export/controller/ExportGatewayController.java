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

package com.icefrog.async.export.controller;

import cn.hutool.core.util.IdUtil;
import com.icefrog.async.export.component.ApiCacheQueue;
import com.icefrog.async.export.dal.entity.SysExportPlan;
import com.icefrog.async.export.dal.mapper.SysExportPlanMapper;
import com.icefrog.async.export.dto.ExportApiReqDto;
import com.icefrog.async.export.integration.enums.PlanStatus;
import com.icefrog.async.export.util.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/***
 * 导出功能业务参数唯一入口网关
 *
 * @author icefrog
 */
@RestController
@RequestMapping("/")
public class ExportGatewayController {

    @Resource
    private SysExportPlanMapper sysExportPlanMapper;

    @GetMapping("/export")
    public ApiResult index(@RequestBody ExportApiReqDto exportApiReqDto) {

        // 请求结果入库.
        exportApiReqDto.setPlanId(IdUtil.fastSimpleUUID());
        // 写入数据库. 确保一致性,当入库失败,不写入内存队列
        SysExportPlan plan = new SysExportPlan();
        plan.setId(exportApiReqDto.getPlanId());
        plan.setUserId(exportApiReqDto.getUserId());
        plan.setBeanId(exportApiReqDto.getBeanId());
        plan.setMethodName(exportApiReqDto.getMethodName());
        plan.setRequestParams(exportApiReqDto.getRequestParams());
        plan.setPlanStatus(PlanStatus.PENDING.getValue());
        plan.setRetryQty(0);
        plan.setTmCreate(new Date());
        plan.setTmSuccess(null);
        plan.setIsDel(0);
        int insertResult = sysExportPlanMapper.insert(plan);
        if(insertResult < 1) {
            return new ApiResult<>().error("导出作业信息入库失败!");
        }

        // 请求参数信息入队
        ApiCacheQueue.offer(exportApiReqDto);

        return new ApiResult<>().success(null);
    }
}

