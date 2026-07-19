package com.company.scopery.modules.integrationhub.exportprofile.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.integrationhub.exportprofile.application.action.ArchiveExportProfileAction;
import com.company.scopery.modules.integrationhub.exportprofile.application.action.CreateExportProfileAction;
import com.company.scopery.modules.integrationhub.exportprofile.application.action.UpdateExportProfileAction;
import com.company.scopery.modules.integrationhub.exportprofile.application.response.ExportProfileResponse;
import com.company.scopery.modules.integrationhub.exportprofile.application.service.ExportProfileQueryService;
import com.company.scopery.modules.integrationhub.exportprofile.http.request.CreateExportProfileRequest;
import com.company.scopery.modules.integrationhub.exportprofile.http.request.UpdateExportProfileRequest;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Integration Hub - Export Profiles")
public class ExportProfileController {
    private final ExportProfileQueryService query;
    private final CreateExportProfileAction create;
    private final UpdateExportProfileAction update;
    private final ArchiveExportProfileAction archive;
    public ExportProfileController(ExportProfileQueryService query, CreateExportProfileAction create,
            UpdateExportProfileAction update, ArchiveExportProfileAction archive) {
        this.query = query; this.create = create; this.update = update; this.archive = archive;
    }
    @PostMapping(IntegrationApiPaths.EXPORT_PROFILES) @Operation(summary = "Create export profile")
    public ApiResponse<ExportProfileResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateExportProfileRequest r) {
        return ApiResponse.success(create.execute(workspaceId, r.profileCode(), r.name(), r.exportFormat(),
                r.targetDestination(), r.objectScope(), r.connectionId()));
    }
    @GetMapping(IntegrationApiPaths.EXPORT_PROFILES) @Operation(summary = "List export profiles")
    public ApiResponse<List<ExportProfileResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }
    @GetMapping(IntegrationApiPaths.EXPORT_PROFILE_BY_ID) @Operation(summary = "Get export profile by id")
    public ApiResponse<ExportProfileResponse> getById(@PathVariable UUID workspaceId, @PathVariable UUID exportProfileId) {
        return ApiResponse.success(query.getById(workspaceId, exportProfileId));
    }
    @PutMapping(IntegrationApiPaths.EXPORT_PROFILE_BY_ID) @Operation(summary = "Update export profile")
    public ApiResponse<ExportProfileResponse> update(@PathVariable UUID workspaceId, @PathVariable UUID exportProfileId,
            @Valid @RequestBody UpdateExportProfileRequest r) {
        return ApiResponse.success(update.execute(workspaceId, exportProfileId, r.name(), r.columnsJson(),
                r.filtersJson(), r.maskingPolicy()));
    }
    @PatchMapping(IntegrationApiPaths.EXPORT_PROFILE_ARCHIVE) @Operation(summary = "Archive export profile")
    public ApiResponse<ExportProfileResponse> archive(@PathVariable UUID workspaceId, @PathVariable UUID exportProfileId) {
        return ApiResponse.success(archive.execute(workspaceId, exportProfileId));
    }
}
