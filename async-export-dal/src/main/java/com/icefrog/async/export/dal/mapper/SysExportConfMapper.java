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

package com.icefrog.async.export.dal.mapper;

import com.icefrog.async.export.dal.entity.SysExportConf;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SysExportConfMapper {
    int deleteByPrimaryKey(String id);

    int insert(SysExportConf record);

    int insertSelective(SysExportConf record);

    SysExportConf selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysExportConf record);

    int updateByPrimaryKey(SysExportConf record);

    SysExportConf queryColumnConfWithBeanId(@Param("beanId") String beanId);
}