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

import com.icefrog.async.export.component.thread.QueueConsumerRunnable;
import com.icefrog.async.export.controller.ExportGatewayController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/***
 * 导出计划队列消费者线程初始化（并启动）优先级低于：初始化数据库待执行导出计划到队列中
 *
 * @see ExportPlanDb2QueueInitEvent
 * @see QueueConsumerRunnable
 * @see ExportGatewayController
 *
 * @author icefrog
 */
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
@Component
public class ExportPlanConsumerQueueInit implements ApplicationRunner {

    @Resource
    private QueueConsumerRunnable queueConsumerRunnable;

    @Override
    public void run(ApplicationArguments args) {

        try {
            Thread thread = new Thread(queueConsumerRunnable);
            thread.setName("QueueConsumerRunnableThread");
            thread.setDaemon(false);
            thread.start();

            log.info("初始化导出计划队列消费线程成功!");
        } catch (Exception ex) {
            log.error("初始化导出计划队列消费线程失败. {}", ex.getMessage(), ex);
        }
    }
}
