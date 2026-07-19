package com.company.scopery.modules.servicesupport.snapshot.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import com.company.scopery.modules.servicesupport.snapshot.application.response.SupportMetricSnapshotResponse;
import com.company.scopery.modules.servicesupport.snapshot.application.service.SupportMetricSnapshotQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - Metric Snapshots")
public class SupportMetricSnapshotController {
    private final SupportMetricSnapshotQueryService query;

    public SupportMetricSnapshotController(SupportMetricSnapshotQueryService query) {
        this.query = query;
    }

    @GetMapping(SupportApiPaths.METRIC_SNAPSHOTS)
    @Operation(summary = "List metric snapshots in workspace")
    public ApiResponse<List<SupportMetricSnapshotResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }
}
