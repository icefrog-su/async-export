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

package com.icefrog.async.export.component;

import com.icefrog.async.export.dto.ExportApiReqDto;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/***
 * Api cache queue. 缓存前端导出计划信息到内存缓存中。 该内存缓存与数据库同步.
 *
 * @author icefrog
 */
public class ApiCacheQueue {

    /***
     * INSTANCE_QUEUE of LinkedBlockingQueue. Default size:200
     *
     * @see BlockingQueue
     * @see LinkedBlockingQueue
     */
    private static BlockingQueue<ExportApiReqDto> INSTANCE_QUEUE = new LinkedBlockingQueue<>(200);

    /***
     * 非阻塞式入队, 区别于阻塞put。 当队列满, 则返回false（表示丢弃）. 否则返回true
     * @param in ExportApiReqDto
     * @return 是否队列满, 队列满则返回false， 且丢弃本次入队数据
     */
    public static boolean offer(@NonNull ExportApiReqDto in) {
        return INSTANCE_QUEUE.offer(in);
    }

    /***
     * 阻塞获取. 当队列为空时阻塞。 直到获取到结果
     * @return ExportApiReqDto
     * @throws InterruptedException
     */
    public static ExportApiReqDto take() throws InterruptedException {
        return INSTANCE_QUEUE.take();
    }

    /***
     * 重新构建内存队列为空。 并指定初始化大小。
     * @param initSize 如果为null，则默认200. 否则初始化大小为指定
     */
    public synchronized static void reconstruction(@Nullable Integer initSize) {
        INSTANCE_QUEUE = new LinkedBlockingQueue<>(initSize == null ? 200 : initSize);
    }

    /***
     * 重新构建内存队列。 并指定初始化内容。
     * @param initCollection 需要初始化的列表内容
     */
    public synchronized static void reconstruction(@NonNull Collection<ExportApiReqDto> initCollection) {
        INSTANCE_QUEUE = new LinkedBlockingQueue<>(initCollection);
    }
}
