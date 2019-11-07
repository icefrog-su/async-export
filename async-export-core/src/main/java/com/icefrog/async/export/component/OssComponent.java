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

package com.icefrog.async.export.component;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/***
 * OSS 相关操作
 *
 * https://help.aliyun.com/document_detail/31827.html
 *
 * @author icefrog
 */
@Slf4j
@Component
public class OssComponent {

    /***
     * oss Endpoint
     */
    @Value("${oss.endpoint}")
    private String endpoint;

    /***
     * oss access key id
     */
    @Value("${oss.accessKeyId}")
    private String accessKeyId;

    /***
     * oss access key secret
     */
    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;

    /***
     * oss bucket
     */
    @Value("${oss.bucket}")
    private String bucket;

    /***
     * oss访问路径
     */
    @Value("${oss.url}")
    private String url;

    /***
     * 上传本地文件到oss
     *
     * @param ossPrefix oss path
     * @param fileName oss file name
     * @param localPath 本地文件绝对路径
     * @return 可直接访问的http url
     */
    public String putObject(String ossPrefix, String fileName, String localPath) {
        OSS ossClient = null;
        try {

            // 创建OSSClient实例。
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, ossPrefix + "/" + fileName, new File(localPath));

            // 上传文件
            ossClient.putObject(putObjectRequest);

            return url + "/" + ossPrefix + "/" + fileName;
        } catch (Exception e) {

            log.error("提交文件到OSS异常. endpoint:{}, accessKeyId:{}, accessKeySecret:{} 异常信息: {}",
                    endpoint, accessKeyId, accessKeySecret, e.getMessage(), e);
            return null;
        } finally {

            if(ossClient != null) {
                // 关闭OSSClient。
                ossClient.shutdown();
            }
        }
    }
}
