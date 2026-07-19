package com.company.scopery.modules.quality.deploymentenv.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.deploymentenv.application.action.ArchiveDeploymentEnvironmentAction;
import com.company.scopery.modules.quality.deploymentenv.application.action.CreateDeploymentEnvironmentAction;
import com.company.scopery.modules.quality.deploymentenv.application.command.CreateDeploymentEnvironmentCommand;
import com.company.scopery.modules.quality.deploymentenv.application.response.DeploymentEnvironmentResponse;
import com.company.scopery.modules.quality.deploymentenv.application.service.DeploymentEnvironmentQueryService;
import com.company.scopery.modules.quality.deploymentenv.http.request.CreateDeploymentEnvironmentRequest;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(QualityApiPaths.DEPLOYMENT_ENVS)
@Tag(name = "Quality - Deployment Environments")
public class DeploymentEnvironmentController {
    private final CreateDeploymentEnvironmentAction create;
    private final ArchiveDeploymentEnvironmentAction archive;
    private final DeploymentEnvironmentQueryService query;
    public DeploymentEnvironmentController(CreateDeploymentEnvironmentAction create, ArchiveDeploymentEnvironmentAction archive, DeploymentEnvironmentQueryService query) {
        this.create=create; this.archive=archive; this.query=query;
    }
    @PostMapping @Operation(summary = "Create deployment environment")
    public ApiResponse<DeploymentEnvironmentResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateDeploymentEnvironmentRequest r) {
        return ApiResponse.success(create.execute(new CreateDeploymentEnvironmentCommand(projectId, r.code(), r.name(), r.environmentType(), r.description())));
    }
    @GetMapping @Operation(summary = "List environments")
    public ApiResponse<List<DeploymentEnvironmentResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
    @GetMapping("/{envId}") @Operation(summary = "Get environment")
    public ApiResponse<DeploymentEnvironmentResponse> get(@PathVariable UUID projectId, @PathVariable UUID envId) {
        return ApiResponse.success(query.get(projectId, envId));
    }
    @PatchMapping("/{envId}/archive") @Operation(summary = "Archive environment")
    public ApiResponse<DeploymentEnvironmentResponse> archive(@PathVariable UUID projectId, @PathVariable UUID envId) {
        return ApiResponse.success(archive.execute(projectId, envId));
    }
}
