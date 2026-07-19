package com.company.scopery.modules.resourcecapacity.workload.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityApiPaths;
import com.company.scopery.modules.resourcecapacity.workload.application.action.CreateWorkloadSnapshotAction;
import com.company.scopery.modules.resourcecapacity.workload.application.response.WorkloadSnapshotApiResponse;
import com.company.scopery.modules.resourcecapacity.workload.application.service.WorkloadSnapshotQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Resource Capacity - Workload Snapshots")
public class WorkloadSnapshotController {
    private final CreateWorkloadSnapshotAction create;
    private final WorkloadSnapshotQueryService query;

    public WorkloadSnapshotController(CreateWorkloadSnapshotAction create, WorkloadSnapshotQueryService query) {
        this.create = create;
        this.query = query;
    }

    @PostMapping(CapacityApiPaths.WORKLOAD_SNAPSHOTS)
    @Operation(summary = "Capture workload snapshot")
    public ApiResponse<WorkloadSnapshotApiResponse> create(@PathVariable UUID projectId) {
        return ApiResponse.success(create.execute(projectId));
    }

    @GetMapping(CapacityApiPaths.WORKLOAD_SNAPSHOTS)
    @Operation(summary = "List workload snapshots")
    public ApiResponse<List<WorkloadSnapshotApiResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.list(projectId));
    }
}
