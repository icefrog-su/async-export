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

package com.icefrog.async.export.integration.export;

import java.io.IOException;
import java.util.List;

/***
 * 导出实现规约,所有导出实现类必须实现此接口. 否则无法被handler调用.
 *
 * @author icefrog
 */
public interface IExport {

    /***
     * 导出操作. 将待导出内容整理为List<T>结果集
     *
     * @param requestParams 请求参数. 一般为页面查询条件
     * @return 待导出内存结果集
     */
    List<BaseResultSet> exportHandler(String requestParams);

    /***
     * 资源释放[可选]. 针对导出时的io释放操作
     * 当派生类未重写此方法时,方法调用将恒定引发UnsupportedOperationException
     *
     * @throws IOException IOException
     */
    default void destory() throws IOException {
        throw new UnsupportedOperationException("This instance does not implement the operation!");
    }
}
