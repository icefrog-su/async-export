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

package com.icefrog.async.export.handler.filemanage.impl;

import cn.hutool.core.io.FileUtil;
import com.icefrog.async.export.handler.filemanage.IFileManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/***
 * 本地文件管理器，将已存在的本地文件转移到指定目录
 *
 * @author icefrog.su@qq.com
 */
@Slf4j
@Component
public class LocalDiskFileManager implements IFileManager {

    @Value("${default.target.dir:data}")
    private String defaultTargetDir;

    /***
     * 将本地文件转移到另一个指定路径。如若已存在，则覆盖
     * @param dir 文件在本地磁盘的绝对路径（文件夹）。
     * @param fileName 文件名。包含后缀
     * @return 最终的文件磁盘地址（包含文件名）
     */
    @Override
    public String process(String dir, String fileName) {

        String sourcePath = dir + File.separator + fileName;
        String targetPath = defaultTargetDir + File.separator + fileName;

        try {
            File targetDir = new File(defaultTargetDir);
            if(!targetDir.exists()) {
                targetDir.mkdirs();
            }

            FileUtil.copy(new File(sourcePath), new File(targetPath), true);
            return targetPath;
        } catch (Exception e) {
            log.error("LocalDiskFileManager转移文件地址失败. absolutePath:{}, fileName:{}, defaultTargetDir:{}",
                    sourcePath, fileName, defaultTargetDir);
            return null;
        }
    }
}