package com.company.scopery.modules.traceability.requirementsource.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.requirementsource.application.action.CreateRequirementSourceAction;
import com.company.scopery.modules.traceability.requirementsource.application.command.CreateRequirementSourceCommand;
import com.company.scopery.modules.traceability.requirementsource.application.response.RequirementSourceResponse;
import com.company.scopery.modules.traceability.requirementsource.application.service.RequirementSourceQueryService;
import com.company.scopery.modules.traceability.requirementsource.http.request.CreateRequirementSourceRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(TraceabilityApiPaths.REQUIREMENT_SOURCES)
@Tag(name = "Traceability - Requirement Sources")
public class RequirementSourceController {
    private final CreateRequirementSourceAction create;
    private final RequirementSourceQueryService query;
    public RequirementSourceController(CreateRequirementSourceAction create, RequirementSourceQueryService query) {
        this.create = create; this.query = query;
    }
    @PostMapping @Operation(summary = "Create requirement source")
    public ApiResponse<RequirementSourceResponse> create(@PathVariable UUID projectId, @PathVariable UUID requirementId,
                                                         @Valid @RequestBody CreateRequirementSourceRequest r) {
        return ApiResponse.success(create.execute(new CreateRequirementSourceCommand(requirementId, r.workspaceId(), r.sourceType(), r.sourceReference(), r.description())));
    }
    @GetMapping @Operation(summary = "List requirement sources")
    public ApiResponse<List<RequirementSourceResponse>> list(@PathVariable UUID projectId, @PathVariable UUID requirementId) {
        return ApiResponse.success(query.list(requirementId));
    }
    @GetMapping("/{sourceId}") @Operation(summary = "Get requirement source")
    public ApiResponse<RequirementSourceResponse> get(@PathVariable UUID projectId, @PathVariable UUID requirementId, @PathVariable UUID sourceId) {
        return ApiResponse.success(query.get(requirementId, sourceId));
    }
}
