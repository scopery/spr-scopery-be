package com.company.scopery.modules.integrationhub.sync.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationApiPaths;
import com.company.scopery.modules.integrationhub.sync.application.action.*;
import com.company.scopery.modules.integrationhub.sync.application.response.SyncJobResponse;
import com.company.scopery.modules.integrationhub.sync.application.service.SyncJobQueryService;
import com.company.scopery.modules.integrationhub.sync.http.request.CreateSyncJobRequest;
import com.company.scopery.modules.integrationhub.sync.http.request.UpdateSyncJobRequest;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map; import java.util.UUID;
@RestController @Tag(name = "Integration Hub - Sync Jobs")
public class SyncJobController {
    private final SyncJobQueryService query;
    private final CreateSyncJobAction create;
    private final UpdateSyncJobAction update;
    private final EnableSyncJobAction enable;
    private final DisableSyncJobAction disable;
    private final ArchiveSyncJobAction archive;
    private final RunSyncJobAction runNow;
    public SyncJobController(SyncJobQueryService query, CreateSyncJobAction create, UpdateSyncJobAction update,
            EnableSyncJobAction enable, DisableSyncJobAction disable, ArchiveSyncJobAction archive,
            RunSyncJobAction runNow) {
        this.query = query; this.create = create; this.update = update;
        this.enable = enable; this.disable = disable; this.archive = archive; this.runNow = runNow;
    }
    @PostMapping(IntegrationApiPaths.SYNC_JOBS) @Operation(summary = "Create sync job")
    public ApiResponse<SyncJobResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateSyncJobRequest r) {
        return ApiResponse.success(create.execute(workspaceId, r.connectionId(), r.name(), r.syncDirection(),
                r.syncMode(), r.objectScope(), r.conflictStrategy()));
    }
    @GetMapping(IntegrationApiPaths.SYNC_JOBS) @Operation(summary = "List sync jobs")
    public ApiResponse<List<SyncJobResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listJobs(workspaceId));
    }
    @GetMapping(IntegrationApiPaths.SYNC_JOB_BY_ID) @Operation(summary = "Get sync job by id")
    public ApiResponse<SyncJobResponse> getById(@PathVariable UUID workspaceId, @PathVariable UUID syncJobId) {
        return ApiResponse.success(query.getJobById(workspaceId, syncJobId));
    }
    @PutMapping(IntegrationApiPaths.SYNC_JOB_BY_ID) @Operation(summary = "Update sync job")
    public ApiResponse<SyncJobResponse> update(@PathVariable UUID workspaceId, @PathVariable UUID syncJobId,
            @Valid @RequestBody UpdateSyncJobRequest r) {
        return ApiResponse.success(update.execute(workspaceId, syncJobId, r.name(), r.syncDirection(),
                r.syncMode(), r.objectScope(), r.conflictStrategy()));
    }
    @PostMapping(IntegrationApiPaths.SYNC_JOB_ENABLE) @Operation(summary = "Enable sync job")
    public ApiResponse<SyncJobResponse> enable(@PathVariable UUID workspaceId, @PathVariable UUID syncJobId) {
        return ApiResponse.success(enable.execute(workspaceId, syncJobId));
    }
    @PostMapping(IntegrationApiPaths.SYNC_JOB_DISABLE) @Operation(summary = "Disable sync job")
    public ApiResponse<SyncJobResponse> disable(@PathVariable UUID workspaceId, @PathVariable UUID syncJobId) {
        return ApiResponse.success(disable.execute(workspaceId, syncJobId));
    }
    @PatchMapping(IntegrationApiPaths.SYNC_JOB_ARCHIVE) @Operation(summary = "Archive sync job")
    public ApiResponse<SyncJobResponse> archive(@PathVariable UUID workspaceId, @PathVariable UUID syncJobId) {
        return ApiResponse.success(archive.execute(workspaceId, syncJobId));
    }
    @PostMapping(IntegrationApiPaths.SYNC_RUN_NOW) @Operation(summary = "Run sync job now")
    public ApiResponse<Map<String, Object>> runNow(@PathVariable UUID workspaceId, @PathVariable UUID syncJobId,
            @RequestParam(defaultValue = "true") boolean success) {
        return ApiResponse.success(runNow.execute(workspaceId, syncJobId, success, "cursor-" + System.currentTimeMillis()));
    }
}
