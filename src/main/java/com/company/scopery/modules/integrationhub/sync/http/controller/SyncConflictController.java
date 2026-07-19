package com.company.scopery.modules.integrationhub.sync.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationApiPaths;
import com.company.scopery.modules.integrationhub.sync.application.action.DismissSyncConflictAction;
import com.company.scopery.modules.integrationhub.sync.application.action.ResolveSyncConflictAction;
import com.company.scopery.modules.integrationhub.sync.application.response.SyncConflictResponse;
import com.company.scopery.modules.integrationhub.sync.application.service.SyncJobQueryService;
import com.company.scopery.modules.integrationhub.sync.http.request.ResolveSyncConflictRequest;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Integration Hub - Sync Conflicts")
public class SyncConflictController {
    private final SyncJobQueryService query;
    private final ResolveSyncConflictAction resolve;
    private final DismissSyncConflictAction dismiss;
    public SyncConflictController(SyncJobQueryService query, ResolveSyncConflictAction resolve, DismissSyncConflictAction dismiss) {
        this.query = query; this.resolve = resolve; this.dismiss = dismiss;
    }
    @GetMapping(IntegrationApiPaths.SYNC_CONFLICTS) @Operation(summary = "List sync conflicts")
    public ApiResponse<List<SyncConflictResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listConflicts(workspaceId));
    }
    @PostMapping(IntegrationApiPaths.SYNC_CONFLICT_RESOLVE) @Operation(summary = "Resolve sync conflict")
    public ApiResponse<SyncConflictResponse> resolve(@PathVariable UUID workspaceId, @PathVariable UUID conflictId,
            @Valid @RequestBody ResolveSyncConflictRequest r) {
        return ApiResponse.success(resolve.execute(workspaceId, conflictId, r.resolutionStrategy(), r.resolutionNotes()));
    }
    @PostMapping(IntegrationApiPaths.SYNC_CONFLICT_DISMISS) @Operation(summary = "Dismiss sync conflict")
    public ApiResponse<SyncConflictResponse> dismiss(@PathVariable UUID workspaceId, @PathVariable UUID conflictId) {
        return ApiResponse.success(dismiss.execute(workspaceId, conflictId));
    }
}
