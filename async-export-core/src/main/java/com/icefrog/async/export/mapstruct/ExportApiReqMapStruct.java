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

package com.icefrog.async.export.mapstruct;

import com.icefrog.async.export.dal.entity.SysExportPlan;
import com.icefrog.async.export.dto.ExportApiReqDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/***
 * SysExportPlan to ExportApiReqDto Map struct
 *
 * @author icefrog
 */
@Mapper
public interface ExportApiReqMapStruct {

    /***
     * MapStruct Instance
     */
    ExportApiReqMapStruct INSTANCE = Mappers.getMapper(ExportApiReqMapStruct.class);

    /***
     * SysExportPlan to ExportApiReqDto
     * @param plan SysExportPlan
     * @return ExportApiReqDto
     */
    @Mappings({
        @Mapping(source = "id", target = "planId"),
        @Mapping(source = "requestParams", target = "requestParams"),
        @Mapping(source = "i18n", target = "i18n"),
        @Mapping(source = "beanId", target = "beanId"),
        @Mapping(source = "methodName", target = "methodName"),
        @Mapping(source = "userId", target = "userId")
    })
    ExportApiReqDto toApiReqDto(SysExportPlan plan);

    /***
     * SysExportPlan List to ExportApiReqDto List
     * @param plans SysExportPlan List
     * @return ExportApiReqDto List
     */
    List<ExportApiReqDto> toApiReqDtos(List<SysExportPlan> plans);
}
