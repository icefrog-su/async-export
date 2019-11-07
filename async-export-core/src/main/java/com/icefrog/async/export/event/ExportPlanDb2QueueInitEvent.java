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

package com.icefrog.async.export.event;

import com.icefrog.async.export.component.ApiCacheQueue;
import com.icefrog.async.export.dal.entity.SysExportPlan;
import com.icefrog.async.export.dal.mapper.SysExportPlanMapper;
import com.icefrog.async.export.integration.enums.PlanStatus;
import com.icefrog.async.export.mapstruct.ExportApiReqMapStruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/***
 * 系统初始化. 初始化数据库中待执行的导出计划到应用内存队列中。初始化失败, 理论不应使系统继续启动.
 *
 * @see ExportPlanConsumerQueueInit
 * @author icefrog
 */
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE - 1)
@Component
public class ExportPlanDb2QueueInitEvent implements ApplicationRunner {

    @Resource
    private SysExportPlanMapper sysExportPlanMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        security(initPlan2Queue());

        log.info("系统初始化执行");
    }

    /***
     * 同步数据库中待执行导出计划到内存队列
     * @return 如果同步失败,返回false. 否则返回true
     */
    public boolean initPlan2Queue() {

        try {
            // 查询数据库中待处理状态计划任务
            List<SysExportPlan> plans = sysExportPlanMapper.queryPlanWithStatus(PlanStatus.PENDING.getValue());

            // 转换为DTO并初始化队列
            ApiCacheQueue.reconstruction(ExportApiReqMapStruct.INSTANCE.toApiReqDtos(plans));
        } catch (Exception ex) {
            log.error("同步数据库中待执行导出计划到内存队列失败. {}", ex.getMessage());
            return false;
        }
        return true;
    }

    /***
     * Security check
     * @param start Terminates the currently running Java Virtual Machine if false.
     */
    public void security(boolean start) {
        if(!start) {
            // Terminates the currently running Java Virtual Machine.
            System.exit(-1);
        }
    }
}
