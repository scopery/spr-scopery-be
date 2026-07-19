package com.company.scopery.modules.integrationhub.exportjob.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.integrationhub.exportjob.application.action.CancelExportJobAction;
import com.company.scopery.modules.integrationhub.exportjob.application.action.CreateExportJobAction;
import com.company.scopery.modules.integrationhub.exportjob.application.response.ExportJobResponse;
import com.company.scopery.modules.integrationhub.exportjob.application.service.ExportJobQueryService;
import com.company.scopery.modules.integrationhub.exportjob.http.request.CreateExportJobRequest;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map; import java.util.UUID;
@RestController @Tag(name = "Integration Hub - Export Jobs")
public class ExportJobController {
    private final ExportJobQueryService query;
    private final CreateExportJobAction create;
    private final CancelExportJobAction cancel;
    public ExportJobController(ExportJobQueryService query, CreateExportJobAction create, CancelExportJobAction cancel) {
        this.query = query; this.create = create; this.cancel = cancel;
    }
    @PostMapping(IntegrationApiPaths.EXPORT_JOBS) @Operation(summary = "Create export job")
    public ApiResponse<ExportJobResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateExportJobRequest r) {
        return ApiResponse.success(create.execute(workspaceId, r.exportFormat(), r.objectScope(), r.projectId(),
                r.exportProfileId(), r.rowCount(), r.reason()));
    }
    @GetMapping(IntegrationApiPaths.EXPORT_JOBS) @Operation(summary = "List export jobs")
    public ApiResponse<List<ExportJobResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }
    @GetMapping(IntegrationApiPaths.EXPORT_JOB_BY_ID) @Operation(summary = "Get export job by id")
    public ApiResponse<ExportJobResponse> getById(@PathVariable UUID workspaceId, @PathVariable UUID exportJobId) {
        return ApiResponse.success(query.getById(workspaceId, exportJobId));
    }
    @PostMapping(IntegrationApiPaths.EXPORT_JOB_CANCEL) @Operation(summary = "Cancel export job")
    public ApiResponse<ExportJobResponse> cancel(@PathVariable UUID workspaceId, @PathVariable UUID exportJobId) {
        return ApiResponse.success(cancel.execute(workspaceId, exportJobId));
    }
    @GetMapping(IntegrationApiPaths.EXPORT_JOB_DOWNLOAD) @Operation(summary = "Download export job (stub)")
    public ApiResponse<Map<String, Object>> download(@PathVariable UUID workspaceId, @PathVariable UUID exportJobId) {
        var job = query.getById(workspaceId, exportJobId);
        return ApiResponse.success(Map.of("fileReference", job.fileReference() == null ? "" : job.fileReference(),
                "note", "Direct download not yet implemented"));
    }
}
