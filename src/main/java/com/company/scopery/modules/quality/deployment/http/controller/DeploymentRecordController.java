package com.company.scopery.modules.quality.deployment.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.deployment.application.action.*;
import com.company.scopery.modules.quality.deployment.application.command.CreateDeploymentRecordCommand;
import com.company.scopery.modules.quality.deployment.application.response.DeploymentRecordResponse;
import com.company.scopery.modules.quality.deployment.application.service.DeploymentRecordQueryService;
import com.company.scopery.modules.quality.deployment.http.request.*;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(QualityApiPaths.DEPLOYMENTS)
@Tag(name = "Quality - Deployments")
public class DeploymentRecordController {
    private final CreateDeploymentRecordAction create;
    private final StartDeploymentRecordAction start;
    private final SucceedDeploymentRecordAction succeed;
    private final FailDeploymentRecordAction fail;
    private final RollbackDeploymentRecordAction rollback;
    private final DeploymentRecordQueryService query;
    public DeploymentRecordController(CreateDeploymentRecordAction create, StartDeploymentRecordAction start, SucceedDeploymentRecordAction succeed,
                              FailDeploymentRecordAction fail, RollbackDeploymentRecordAction rollback, DeploymentRecordQueryService query) {
        this.create=create; this.start=start; this.succeed=succeed; this.fail=fail; this.rollback=rollback; this.query=query;
    }
    @PostMapping @Operation(summary = "Create deployment")
    public ApiResponse<DeploymentRecordResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateDeploymentRecordRequest r) {
        return ApiResponse.success(create.execute(new CreateDeploymentRecordCommand(projectId, r.releasePackageId(), r.deploymentEnvironmentId(), r.buildReference(), r.deploymentReference(), r.rollbackPlanId())));
    }
    @GetMapping @Operation(summary = "List deployments")
    public ApiResponse<List<DeploymentRecordResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
    @GetMapping("/{deploymentId}") @Operation(summary = "Get deployment")
    public ApiResponse<DeploymentRecordResponse> get(@PathVariable UUID projectId, @PathVariable UUID deploymentId) {
        return ApiResponse.success(query.get(projectId, deploymentId));
    }
    @PostMapping("/{deploymentId}/start") @Operation(summary = "Start deployment")
    public ApiResponse<DeploymentRecordResponse> start(@PathVariable UUID projectId, @PathVariable UUID deploymentId) {
        return ApiResponse.success(start.execute(projectId, deploymentId));
    }
    @PostMapping("/{deploymentId}/succeed") @Operation(summary = "Mark succeeded")
    public ApiResponse<DeploymentRecordResponse> succeed(@PathVariable UUID projectId, @PathVariable UUID deploymentId) {
        return ApiResponse.success(succeed.execute(projectId, deploymentId));
    }
    @PostMapping("/{deploymentId}/fail") @Operation(summary = "Mark failed")
    public ApiResponse<DeploymentRecordResponse> fail(@PathVariable UUID projectId, @PathVariable UUID deploymentId, @Valid @RequestBody FailDeploymentRequest r) {
        return ApiResponse.success(fail.execute(projectId, deploymentId, r.failureReason()));
    }
    @PostMapping("/{deploymentId}/rollback") @Operation(summary = "Rollback deployment")
    public ApiResponse<DeploymentRecordResponse> rollback(@PathVariable UUID projectId, @PathVariable UUID deploymentId, @Valid @RequestBody RollbackDeploymentRequest r) {
        return ApiResponse.success(rollback.execute(projectId, deploymentId, r.rollbackReason()));
    }
}
