package com.company.scopery.modules.integrationhub.mapping.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.integrationhub.mapping.application.action.ArchiveDataMappingProfileAction;
import com.company.scopery.modules.integrationhub.mapping.application.action.CreateDataMappingProfileAction;
import com.company.scopery.modules.integrationhub.mapping.application.action.UpdateDataMappingProfileAction;
import com.company.scopery.modules.integrationhub.mapping.application.response.DataMappingProfileResponse;
import com.company.scopery.modules.integrationhub.mapping.application.response.ExternalIdMappingResponse;
import com.company.scopery.modules.integrationhub.mapping.application.service.DataMappingProfileQueryService;
import com.company.scopery.modules.integrationhub.mapping.http.request.CreateDataMappingProfileRequest;
import com.company.scopery.modules.integrationhub.mapping.http.request.UpdateDataMappingProfileRequest;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Integration Hub - Mapping Profiles")
public class DataMappingProfileController {
    private final DataMappingProfileQueryService query;
    private final CreateDataMappingProfileAction create;
    private final UpdateDataMappingProfileAction update;
    private final ArchiveDataMappingProfileAction archive;
    public DataMappingProfileController(DataMappingProfileQueryService query, CreateDataMappingProfileAction create,
            UpdateDataMappingProfileAction update, ArchiveDataMappingProfileAction archive) {
        this.query = query; this.create = create; this.update = update; this.archive = archive;
    }
    @PostMapping(IntegrationApiPaths.MAPPING_PROFILES) @Operation(summary = "Create mapping profile")
    public ApiResponse<DataMappingProfileResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateDataMappingProfileRequest r) {
        return ApiResponse.success(create.execute(workspaceId, r.mappingCode(), r.name(), r.targetObjectType(),
                r.sourceFormat(), r.mappingJson(), r.connectionId()));
    }
    @GetMapping(IntegrationApiPaths.MAPPING_PROFILES) @Operation(summary = "List mapping profiles")
    public ApiResponse<List<DataMappingProfileResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }
    @GetMapping(IntegrationApiPaths.MAPPING_PROFILE_BY_ID) @Operation(summary = "Get mapping profile by id")
    public ApiResponse<DataMappingProfileResponse> getById(@PathVariable UUID workspaceId, @PathVariable UUID mappingProfileId) {
        return ApiResponse.success(query.getById(workspaceId, mappingProfileId));
    }
    @PutMapping(IntegrationApiPaths.MAPPING_PROFILE_BY_ID) @Operation(summary = "Update mapping profile")
    public ApiResponse<DataMappingProfileResponse> update(@PathVariable UUID workspaceId, @PathVariable UUID mappingProfileId,
            @Valid @RequestBody UpdateDataMappingProfileRequest r) {
        return ApiResponse.success(update.execute(workspaceId, mappingProfileId, r.name(), r.mappingJson(), r.validationRulesJson()));
    }
    @PatchMapping(IntegrationApiPaths.MAPPING_PROFILE_ARCHIVE) @Operation(summary = "Archive mapping profile")
    public ApiResponse<DataMappingProfileResponse> archive(@PathVariable UUID workspaceId, @PathVariable UUID mappingProfileId) {
        return ApiResponse.success(archive.execute(workspaceId, mappingProfileId));
    }
    @GetMapping(IntegrationApiPaths.EXTERNAL_ID_MAPPINGS) @Operation(summary = "List external id mappings")
    public ApiResponse<List<ExternalIdMappingResponse>> listExternalIdMappings(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listExternalIdMappings(workspaceId));
    }
}
