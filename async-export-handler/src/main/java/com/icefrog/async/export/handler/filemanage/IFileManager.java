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

package com.icefrog.async.export.handler.filemanage;

/***
 * 文件处理接口。 系统将在文件写入本地磁盘后调用此接口upload方法。 实际文件数据流向取决于具体实现。如OSS、FTP、本地磁盘等
 *
 * @author icefrog.su@qq.com
 */
public interface IFileManager {

    /***
     *
     * 文件处理。控制一个已存在于服务器上的本地文件最终数据流向
     *
     * @param dir 文件在本地磁盘的绝对路径（文件夹）。
     * @param fileName 文件名。包含后缀
     * @return 最终文件访问地址，可能是存在于网络上任意位置的url地址，也可能是本地磁盘的盘符描述的文件路径
     */
    String process(String dir, String fileName);

}
