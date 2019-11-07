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

package com.icefrog.async.export.handler.dictionary;

/***
 * 字典值替换。针对不同的字典值替换实现均实现该接口
 *
 * @author icefrog.su@qq.com
 */
public interface IDictionaryReplace {

    /***
     * 字典值替换逻辑
     * @param code 字典值字段。
     * @param beforeValue 替换前的字典值
     * @return 替换后的结果
     */
    String replace(String code, String beforeValue);

}
