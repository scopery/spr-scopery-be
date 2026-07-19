package com.company.scopery.modules.integrationhub.importjob.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.integrationhub.importjob.application.action.*;
import com.company.scopery.modules.integrationhub.importjob.application.response.ImportJobResponse;
import com.company.scopery.modules.integrationhub.importjob.application.response.ImportRowResultResponse;
import com.company.scopery.modules.integrationhub.importjob.application.service.ImportJobQueryService;
import com.company.scopery.modules.integrationhub.importjob.http.request.CreateImportJobRequest;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Integration Hub - Import Jobs")
public class ImportJobController {
    private final ImportJobQueryService query;
    private final CreateImportJobAction create;
    private final ValidateImportJobAction validate;
    private final DryRunImportJobAction dryRun;
    private final ExecuteImportJobAction execute;
    private final CancelImportJobAction cancel;
    public ImportJobController(ImportJobQueryService query, CreateImportJobAction create,
            ValidateImportJobAction validate, DryRunImportJobAction dryRun,
            ExecuteImportJobAction execute, CancelImportJobAction cancel) {
        this.query = query; this.create = create; this.validate = validate;
        this.dryRun = dryRun; this.execute = execute; this.cancel = cancel;
    }
    @PostMapping(IntegrationApiPaths.IMPORT_JOBS) @Operation(summary = "Create import job")
    public ApiResponse<ImportJobResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateImportJobRequest r) {
        return ApiResponse.success(create.execute(workspaceId, r.jobMode(), r.sourceFormat(), r.targetObjectType()));
    }
    @GetMapping(IntegrationApiPaths.IMPORT_JOBS) @Operation(summary = "List import jobs")
    public ApiResponse<List<ImportJobResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }
    @GetMapping(IntegrationApiPaths.IMPORT_JOB_BY_ID) @Operation(summary = "Get import job by id")
    public ApiResponse<ImportJobResponse> getById(@PathVariable UUID workspaceId, @PathVariable UUID importJobId) {
        return ApiResponse.success(query.getById(workspaceId, importJobId));
    }
    @PostMapping(IntegrationApiPaths.IMPORT_JOB_VALIDATE) @Operation(summary = "Validate import job")
    public ApiResponse<ImportJobResponse> validate(@PathVariable UUID workspaceId, @PathVariable UUID importJobId) {
        return ApiResponse.success(validate.execute(workspaceId, importJobId, 2L, 2L, 0L));
    }
    @PostMapping(IntegrationApiPaths.IMPORT_JOB_DRY_RUN) @Operation(summary = "Dry-run import job")
    public ApiResponse<ImportJobResponse> dryRun(@PathVariable UUID workspaceId, @PathVariable UUID importJobId) {
        return ApiResponse.success(dryRun.execute(workspaceId, importJobId));
    }
    @PostMapping(IntegrationApiPaths.IMPORT_JOB_EXECUTE) @Operation(summary = "Execute import job")
    public ApiResponse<ImportJobResponse> execute(@PathVariable UUID workspaceId, @PathVariable UUID importJobId) {
        return ApiResponse.success(execute.execute(workspaceId, importJobId, 2L));
    }
    @PostMapping(IntegrationApiPaths.IMPORT_JOB_CANCEL) @Operation(summary = "Cancel import job")
    public ApiResponse<ImportJobResponse> cancel(@PathVariable UUID workspaceId, @PathVariable UUID importJobId) {
        return ApiResponse.success(cancel.execute(workspaceId, importJobId));
    }
    @GetMapping(IntegrationApiPaths.IMPORT_JOB_ROWS) @Operation(summary = "List import job rows")
    public ApiResponse<List<ImportRowResultResponse>> listRows(@PathVariable UUID workspaceId, @PathVariable UUID importJobId) {
        return ApiResponse.success(query.listRows(workspaceId, importJobId));
    }
}
