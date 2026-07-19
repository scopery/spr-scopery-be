package com.company.scopery.modules.scope.criteria.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.scope.criteria.application.action.CreateAcceptanceCriteriaAction;
import com.company.scopery.modules.scope.criteria.application.action.SatisfyAcceptanceCriteriaAction;
import com.company.scopery.modules.scope.criteria.application.action.WaiveAcceptanceCriteriaAction;
import com.company.scopery.modules.scope.criteria.application.command.CreateAcceptanceCriteriaCommand;
import com.company.scopery.modules.scope.criteria.application.command.SatisfyAcceptanceCriteriaCommand;
import com.company.scopery.modules.scope.criteria.application.command.WaiveAcceptanceCriteriaCommand;
import com.company.scopery.modules.scope.criteria.application.response.AcceptanceCriteriaResponse;
import com.company.scopery.modules.scope.criteria.application.service.AcceptanceCriteriaQueryService;
import com.company.scopery.modules.scope.criteria.http.request.CreateAcceptanceCriteriaRequest;
import com.company.scopery.modules.scope.criteria.http.request.WaiveCriteriaRequest;
import com.company.scopery.modules.scope.shared.constant.ScopeApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Scope - Acceptance Criteria")
public class AcceptanceCriteriaController {
    private final CreateAcceptanceCriteriaAction create;
    private final SatisfyAcceptanceCriteriaAction satisfy;
    private final WaiveAcceptanceCriteriaAction waive;
    private final AcceptanceCriteriaQueryService query;

    public AcceptanceCriteriaController(CreateAcceptanceCriteriaAction create,
                                        SatisfyAcceptanceCriteriaAction satisfy,
                                        WaiveAcceptanceCriteriaAction waive,
                                        AcceptanceCriteriaQueryService query) {
        this.create = create;
        this.satisfy = satisfy;
        this.waive = waive;
        this.query = query;
    }

    @PostMapping(ScopeApiPaths.DELIVERABLES + "/{deliverableId}/acceptance-criteria")
    @Operation(summary = "Create acceptance criteria")
    public ApiResponse<AcceptanceCriteriaResponse> create(@PathVariable UUID projectId,
                                                          @PathVariable UUID deliverableId,
                                                          @Valid @RequestBody CreateAcceptanceCriteriaRequest request) {
        return ApiResponse.success(create.execute(new CreateAcceptanceCriteriaCommand(
                projectId, deliverableId, request.title(), request.type(),
                request.description(), request.mandatory())));
    }

    @GetMapping(ScopeApiPaths.DELIVERABLES + "/{deliverableId}/acceptance-criteria")
    @Operation(summary = "List acceptance criteria")
    public ApiResponse<List<AcceptanceCriteriaResponse>> list(@PathVariable UUID projectId,
                                                              @PathVariable UUID deliverableId) {
        return ApiResponse.success(query.listByDeliverable(projectId, deliverableId));
    }

    @GetMapping(ScopeApiPaths.CRITERIA + "/{criteriaId}")
    @Operation(summary = "Get acceptance criteria")
    public ApiResponse<AcceptanceCriteriaResponse> get(@PathVariable UUID projectId, @PathVariable UUID criteriaId) {
        return ApiResponse.success(query.get(projectId, criteriaId));
    }

    @PatchMapping(ScopeApiPaths.CRITERIA + "/{criteriaId}/satisfy")
    @Operation(summary = "Satisfy criteria")
    public ApiResponse<AcceptanceCriteriaResponse> satisfy(@PathVariable UUID projectId, @PathVariable UUID criteriaId) {
        return ApiResponse.success(satisfy.execute(new SatisfyAcceptanceCriteriaCommand(projectId, criteriaId)));
    }

    @PatchMapping(ScopeApiPaths.CRITERIA + "/{criteriaId}/waive")
    @Operation(summary = "Waive criteria")
    public ApiResponse<AcceptanceCriteriaResponse> waive(@PathVariable UUID projectId,
                                                         @PathVariable UUID criteriaId,
                                                         @Valid @RequestBody WaiveCriteriaRequest request) {
        return ApiResponse.success(waive.execute(new WaiveAcceptanceCriteriaCommand(
                projectId, criteriaId, request.reason())));
    }
}
