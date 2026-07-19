package com.company.scopery.modules.traceability.requirementcriteria.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.requirementcriteria.application.action.CreateRequirementCriteriaAction;
import com.company.scopery.modules.traceability.requirementcriteria.application.command.CreateRequirementCriteriaCommand;
import com.company.scopery.modules.traceability.requirementcriteria.application.response.RequirementCriteriaResponse;
import com.company.scopery.modules.traceability.requirementcriteria.application.service.RequirementCriteriaQueryService;
import com.company.scopery.modules.traceability.requirementcriteria.http.request.CreateRequirementCriteriaRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(TraceabilityApiPaths.REQUIREMENT_CRITERIA)
@Tag(name = "Traceability - Requirement Criteria")
public class RequirementCriteriaController {
    private final CreateRequirementCriteriaAction create;
    private final RequirementCriteriaQueryService query;
    public RequirementCriteriaController(CreateRequirementCriteriaAction create, RequirementCriteriaQueryService query) {
        this.create = create; this.query = query;
    }
    @PostMapping @Operation(summary = "Create acceptance criterion")
    public ApiResponse<RequirementCriteriaResponse> create(@PathVariable UUID projectId, @PathVariable UUID requirementId,
                                                           @Valid @RequestBody CreateRequirementCriteriaRequest r) {
        return ApiResponse.success(create.execute(new CreateRequirementCriteriaCommand(requirementId, r.workspaceId(), r.description(), r.acceptanceType(), r.displayOrder())));
    }
    @GetMapping @Operation(summary = "List acceptance criteria")
    public ApiResponse<List<RequirementCriteriaResponse>> list(@PathVariable UUID projectId, @PathVariable UUID requirementId) {
        return ApiResponse.success(query.list(requirementId));
    }
    @GetMapping("/{criteriaId}") @Operation(summary = "Get acceptance criterion")
    public ApiResponse<RequirementCriteriaResponse> get(@PathVariable UUID projectId, @PathVariable UUID requirementId, @PathVariable UUID criteriaId) {
        return ApiResponse.success(query.get(requirementId, criteriaId));
    }
}
