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

package com.icefrog.async.export.handler.dto;

import com.icefrog.async.export.integration.annotation.DictionaryScan;
import com.icefrog.async.export.integration.export.BaseResultSet;
import lombok.Data;

/***
 * 示例Dto。 必须继承BaseResultSet
 *
 * @author icefrog
 */
@Data
public class ExampleDto extends BaseResultSet {

    private String testName;

    /***
     * 表示需要扫描redis/db中字典表code为指定的数据。 并且替换返回。 支持国际化
     */
    @DictionaryScan(code = "SEX")
    protected String testContent;

}
