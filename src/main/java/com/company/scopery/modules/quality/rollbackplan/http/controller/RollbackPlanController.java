package com.company.scopery.modules.quality.rollbackplan.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.rollbackplan.application.action.ApproveRollbackPlanAction;
import com.company.scopery.modules.quality.rollbackplan.application.action.CreateRollbackPlanAction;
import com.company.scopery.modules.quality.rollbackplan.application.command.CreateRollbackPlanCommand;
import com.company.scopery.modules.quality.rollbackplan.application.response.RollbackPlanResponse;
import com.company.scopery.modules.quality.rollbackplan.application.service.RollbackPlanQueryService;
import com.company.scopery.modules.quality.rollbackplan.http.request.CreateRollbackPlanRequest;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(QualityApiPaths.ROLLBACK_PLANS)
@Tag(name = "Quality - Rollback Plans")
public class RollbackPlanController {
    private final CreateRollbackPlanAction create;
    private final ApproveRollbackPlanAction approve;
    private final RollbackPlanQueryService query;
    public RollbackPlanController(CreateRollbackPlanAction create, ApproveRollbackPlanAction approve, RollbackPlanQueryService query) {
        this.create=create; this.approve=approve; this.query=query;
    }
    @PostMapping @Operation(summary = "Create rollback plan")
    public ApiResponse<RollbackPlanResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateRollbackPlanRequest r) {
        return ApiResponse.success(create.execute(new CreateRollbackPlanCommand(projectId, r.releasePackageId(), r.deploymentRecordId(), r.title(), r.description(), r.ownerUserId(), r.stepsJson())));
    }
    @GetMapping @Operation(summary = "List rollback plans")
    public ApiResponse<List<RollbackPlanResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
    @GetMapping("/{planId}") @Operation(summary = "Get rollback plan")
    public ApiResponse<RollbackPlanResponse> get(@PathVariable UUID projectId, @PathVariable UUID planId) {
        return ApiResponse.success(query.get(projectId, planId));
    }
    @PostMapping("/{planId}/approve") @Operation(summary = "Approve rollback plan")
    public ApiResponse<RollbackPlanResponse> approve(@PathVariable UUID projectId, @PathVariable UUID planId) {
        return ApiResponse.success(approve.execute(projectId, planId));
    }
}
