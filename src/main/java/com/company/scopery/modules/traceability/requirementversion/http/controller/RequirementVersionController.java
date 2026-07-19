package com.company.scopery.modules.traceability.requirementversion.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.requirementversion.application.action.CreateRequirementVersionAction;
import com.company.scopery.modules.traceability.requirementversion.application.command.CreateRequirementVersionCommand;
import com.company.scopery.modules.traceability.requirementversion.application.response.RequirementVersionResponse;
import com.company.scopery.modules.traceability.requirementversion.application.service.RequirementVersionQueryService;
import com.company.scopery.modules.traceability.requirementversion.http.request.CreateRequirementVersionRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(TraceabilityApiPaths.REQUIREMENT_VERSIONS)
@Tag(name = "Traceability - Requirement Versions")
public class RequirementVersionController {
    private final CreateRequirementVersionAction create;
    private final RequirementVersionQueryService query;
    public RequirementVersionController(CreateRequirementVersionAction create, RequirementVersionQueryService query) {
        this.create = create; this.query = query;
    }
    @PostMapping @Operation(summary = "Create requirement version")
    public ApiResponse<RequirementVersionResponse> create(@PathVariable UUID projectId, @PathVariable UUID requirementId,
                                                          @Valid @RequestBody CreateRequirementVersionRequest r) {
        return ApiResponse.success(create.execute(new CreateRequirementVersionCommand(requirementId, r.workspaceId(), r.title(), r.description(), r.changeSummary(), r.createdByUserId())));
    }
    @GetMapping @Operation(summary = "List requirement versions")
    public ApiResponse<List<RequirementVersionResponse>> list(@PathVariable UUID projectId, @PathVariable UUID requirementId) {
        return ApiResponse.success(query.list(requirementId));
    }
    @GetMapping("/{versionId}") @Operation(summary = "Get requirement version")
    public ApiResponse<RequirementVersionResponse> get(@PathVariable UUID projectId, @PathVariable UUID requirementId, @PathVariable UUID versionId) {
        return ApiResponse.success(query.get(requirementId, versionId));
    }
}
