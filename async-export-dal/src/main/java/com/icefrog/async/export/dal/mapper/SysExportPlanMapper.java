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

import com.icefrog.async.export.dal.entity.SysExportPlan;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

/***
 * 系统导出计划Repository mapper. 区别于其他业务实现
 *
 * @author icefrog
 */
@Repository
public interface SysExportPlanMapper {
    int deleteByPrimaryKey(String id);

    int insert(SysExportPlan record);

    int insertSelective(SysExportPlan record);

    SysExportPlan selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysExportPlan record);

    int updateByPrimaryKey(SysExportPlan record);

    List<SysExportPlan> queryPlanWithStatus(@NonNull @Param("planStatus") String planStatus);
}