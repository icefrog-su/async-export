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

package com.icefrog.async.export.integration.annotation;

import java.lang.annotation.*;

/***
 * 字典扫描注解. 用于从redis/db中扫描需要替换的字典值, 支持国际化.
 *
 * @author icefrog
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictionaryScan {

    /***
     * 字典表字段.
     * @return 描述该字典字段名称（redis/db）
     */
    String code();

    /***
     * 标记是否需要扫描。 默认为true
     * @return 如果为false。则忽略扫描需求。
     */
    boolean scan() default true;

}
