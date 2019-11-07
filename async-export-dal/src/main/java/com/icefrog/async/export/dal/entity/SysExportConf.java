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
 * 系统导出配置表
 *
 * @author icefrog
 */
@Data
public class SysExportConf {

    /***
     * 配置ID
     */
    private String id;

    /***
     * 配置项JSON
     */
    private String columnConfJson;

    /***
     * Spring bean id
     */
    private String beanId;

    /***
     * Spring bean's method name
     */
    private String methodName;

    /***
     * Create id
     */
    private Long createId;

    /***
     * Create time
     */
    private Date tmCreate;

    /***
     * Update id
     */
    private Long updateId;

    /***
     * Update time
     */
    private Date tmUpdate;

    /***
     * Config's remark
     */
    private String remark;

    /***
     * Mark delete or not . 0：delete(logic), 1：not delete
     */
    private Integer isDel;
}