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

package com.icefrog.async.export.util;

import com.alibaba.excel.EasyExcel;

import java.util.List;

/***
 * Excel Utils.
 *
 * @see EasyExcel
 * @author icefrog
 */
public class ExcelUtil {

    /***
     * 动态头部excel写入
     * @param absolutePath xlsx文件绝对路径
     * @param sheetName sheet name
     * @param head 动态头
     * @param data 数据体
     */
    public static void dynamicHeadWrite(String absolutePath, String sheetName, List<List<String>> head, List<List<String>> data) {
        EasyExcel.write(absolutePath)
                 .head(head)
                 .sheet(sheetName)
                 .doWrite(data);
    }
}
