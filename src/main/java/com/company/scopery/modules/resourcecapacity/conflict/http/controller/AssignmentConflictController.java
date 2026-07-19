package com.company.scopery.modules.resourcecapacity.conflict.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.resourcecapacity.conflict.application.action.AcknowledgeAssignmentConflictAction;
import com.company.scopery.modules.resourcecapacity.conflict.application.action.DetectAssignmentConflictAction;
import com.company.scopery.modules.resourcecapacity.conflict.application.action.RecalculateAssignmentConflictsAction;
import com.company.scopery.modules.resourcecapacity.conflict.application.response.AssignmentConflictApiResponse;
import com.company.scopery.modules.resourcecapacity.conflict.application.service.AssignmentConflictQueryService;
import com.company.scopery.modules.resourcecapacity.conflict.http.request.CreateAssignmentConflictRequest;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Resource Capacity - Assignment Conflicts")
public class AssignmentConflictController {
    private final DetectAssignmentConflictAction detect;
    private final AcknowledgeAssignmentConflictAction acknowledge;
    private final RecalculateAssignmentConflictsAction recalculate;
    private final AssignmentConflictQueryService query;

    public AssignmentConflictController(
            DetectAssignmentConflictAction detect,
            AcknowledgeAssignmentConflictAction acknowledge,
            RecalculateAssignmentConflictsAction recalculate,
            AssignmentConflictQueryService query) {
        this.detect = detect;
        this.acknowledge = acknowledge;
        this.recalculate = recalculate;
        this.query = query;
    }

    @PostMapping(CapacityApiPaths.CONFLICTS)
    @Operation(summary = "Detect/register assignment conflict")
    public ApiResponse<AssignmentConflictApiResponse> detect(
            @PathVariable UUID projectId, @Valid @RequestBody CreateAssignmentConflictRequest r) {
        return ApiResponse.success(detect.execute(projectId, r));
    }

    @GetMapping(CapacityApiPaths.CONFLICTS)
    @Operation(summary = "List assignment conflicts")
    public ApiResponse<List<AssignmentConflictApiResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.list(projectId));
    }

    @PostMapping(CapacityApiPaths.CONFLICTS + "/{conflictId}/acknowledge")
    @Operation(summary = "Acknowledge assignment conflict")
    public ApiResponse<AssignmentConflictApiResponse> acknowledge(
            @PathVariable UUID projectId, @PathVariable UUID conflictId) {
        return ApiResponse.success(acknowledge.execute(projectId, conflictId, null));
    }

    @PostMapping(CapacityApiPaths.CONFLICTS_RECALCULATE)
    @Operation(summary = "Recalculate assignment conflicts")
    public ApiResponse<List<AssignmentConflictApiResponse>> recalculate(@PathVariable UUID projectId) {
        return ApiResponse.success(recalculate.execute(projectId));
    }
}
