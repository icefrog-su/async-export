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

package com.icefrog.async.export.integration.enums;

import lombok.Getter;

/***
 * 计划状态枚举
 *
 * @author icefrog
 */
@Getter
public enum PlanStatus {

    /***
     * 待执行枚举
     */
    PENDING("待执行","执行状态：暂未执行"),

    /***
     * 执行成功枚举
     */
    SUCCESS("执行成功", "执行状态：执行成功"),

    /***
     * 执行失败枚举
     */
    FAILED("执行失败", "执行状态：执行失败");



    ///////////////////// 属性 /////////////////////

    /***
     * 数据库对应字段
     */
    private String value;

    /***
     * 字段描述
     */
    private String desc;

    PlanStatus(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
