/***
 * Copyright 2019 icefrog
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

import java.io.Serializable;

/***
 * ApiResult. http response object
 *
 * @author icefrog.su@qq.com
 */
public class ApiResult<T extends Object> implements Serializable {
    private static final long serialVersionUID = -3624770785365476720L;
    
	public static final Integer CODE_SUCCESS        	= 0;  // 响应成功
	public static final Integer CODE_FAILED         	= 1;  // 响应失败
	public static final String  SUCCESS_MESSAGE     	= "success";
	public static final String  FAILED_MESSAGE      	= "failed";
    
    private String message;
	
	private Integer code;
	
	private T data;
    
    /***
     * 构建一个描述正确状态的ApiResult(code=ApiResult.CODE_SUCCESS)
     * 并指定需要返回的数据对象
     * message字段为默认值(message=ApiResult.SUCCESS_MESSAGE)
     * @param data 需要返回的数据对象
     * @return ApiResult object with success status
     */
	public ApiResult<T> success(T data){
        return new ApiResult<T>().setCode(ApiResult.CODE_SUCCESS).setMessage(ApiResult.SUCCESS_MESSAGE).setData(data);
    }
    
    /***
     * 构建一个描述正确状态的ApiResult(code=ApiResult.CODE_SUCCESS)
     * 并指定需要返回的数据对象
     * @param data 需要返回的数据对象
     * @param message 自定义消息
     * @return ApiResult object with success status
     */
    public ApiResult<T> success(String message, T data){
        return new ApiResult<T>().setCode(ApiResult.CODE_SUCCESS).setMessage(message).setData(data);
    }
    
    /***
     * 构建一个描述错误在的ApiResult(code=ApiResult.CODE_FAILED)
     * 并指定需要返回的数据对象
     * @param data 需要返回的数据对象
     * @return ApiResult object with error status
     */
    public ApiResult<T> error(T data){
        return new ApiResult<T>().setCode(ApiResult.CODE_FAILED).setMessage(ApiResult.FAILED_MESSAGE).setData(data);
    }
    
    /***
     * 构建一个描述错误在的ApiResult(code=ApiResult.CODE_FAILED)
     * 并指定需要返回的数据对象
     * @param message 自定义消息
     * @param data 需要返回的数据对象
     * @return ApiResult object with error status
     */
    public ApiResult<T>  error(String message, T data){
        return new ApiResult<T>().setCode(ApiResult.CODE_FAILED).setMessage(message).setData(data);
    }
    
    public ApiResult<T> error(){
        return this.error(null);
    }
	
    public boolean isSuccess(){
        return this.code == null ? false : this.code.intValue() == CODE_SUCCESS.intValue();
    }
    
    public boolean isError(){
        return !isSuccess();
    }

	public String getMessage() {
		return message;
	}

	public ApiResult setMessage(String message) {
		this.message = message;
		return this;
	}

	public Integer getCode() {
		return code;
	}

	public ApiResult setCode(Integer code) {
		this.code = code;
		return this;
	}

	public T getData() {
		return data;
	}

	public ApiResult setData(T data) {
		this.data = data;
		return this;
	}
}