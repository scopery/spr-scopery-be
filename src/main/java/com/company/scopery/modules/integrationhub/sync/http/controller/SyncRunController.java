package com.company.scopery.modules.integrationhub.sync.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationApiPaths;
import com.company.scopery.modules.integrationhub.sync.application.response.SyncRunResponse;
import com.company.scopery.modules.integrationhub.sync.application.service.SyncJobQueryService;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Integration Hub - Sync Runs")
public class SyncRunController {
    private final SyncJobQueryService query;
    public SyncRunController(SyncJobQueryService query) { this.query = query; }
    @GetMapping(IntegrationApiPaths.SYNC_RUNS) @Operation(summary = "List sync runs")
    public ApiResponse<List<SyncRunResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listRuns(workspaceId));
    }
    @GetMapping(IntegrationApiPaths.SYNC_RUN_BY_ID) @Operation(summary = "Get sync run by id")
    public ApiResponse<SyncRunResponse> getById(@PathVariable UUID workspaceId, @PathVariable UUID syncRunId) {
        return ApiResponse.success(query.getRunById(workspaceId, syncRunId));
    }
}
