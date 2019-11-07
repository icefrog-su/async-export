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

package com.icefrog.async.export.handler;

import com.icefrog.async.export.handler.dto.ExampleDto;
import com.icefrog.async.export.integration.export.BaseResultSet;
import com.icefrog.async.export.integration.export.IExport;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/***
 * 导出handler示例.
 *
 * @author icefrog
 */
@Component("exampleHandler")
public class ExampleHandler implements IExport {

    /***
     * 一般此处查询数据库中待导出的结果集。整理成List并且泛型为继承自BaseResultSet的任意POJO
     *
     * @param requestParams 请求参数. 一般为页面查询条件
     * @return 待导出的内存数据集
     */
    @Override
    public List<BaseResultSet> exportHandler(String requestParams) {

        List<BaseResultSet> rs = new ArrayList<>();

        ExampleDto d1 = new ExampleDto();
        d1.setTestContent("9");
        d1.setTestName("222");

        ExampleDto d2 = new ExampleDto();
        d2.setTestContent("444");
        d2.setTestName("555");

        rs.add(d1);
        rs.add(d2);

        return rs;
    }
}
