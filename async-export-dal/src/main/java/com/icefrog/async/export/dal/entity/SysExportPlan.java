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

package com.icefrog.async.export.dal.entity;

import lombok.Data;

import java.util.Date;

/***
 * 系统导出计划实体类. 区别于其他业务实现
 *
 * @author icefrog
 */
@Data
public class SysExportPlan {

    /***
     * 计划ID
     */
    private String id;

    /***
     * 用户ID
     */
    private Long userId;

    /***
     * Spring bean id
     */
    private String beanId;

    /***
     * Spring bean's method name
     */
    private String methodName;

    /***
     * Page query params
     */
    private String requestParams;

    /***
     * Data line count
     */
    private Long lineCount;

    /***
     * oss url
     */
    private String url;

    /***
     * 计划状态：待执行、执行成功、执行失败
     */
    private String planStatus;

    /***
     * 失败重试次数
     */
    private Integer retryQty;

    /***
     * 国际化标记符
     */
    private String i18n;

    /***
     * 失败状态下的错误消息
     */
    private String failedMsg;

    /***
     * Create time
     */
    private Date tmCreate;

    /***
     * Success time
     */
    private Date tmSuccess;

    /***
     * Mark delete or not . 0：delete(logic), 1：not delete
     */
    private Integer isDel;
}