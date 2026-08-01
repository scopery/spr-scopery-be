package com.company.scopery.modules.traceability.usecase.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import com.company.scopery.modules.traceability.usecase.application.query.GetPrimaryFunctionChangeImpactQuery;
import com.company.scopery.modules.traceability.usecase.application.query.GetUseCaseFlowScopeQuery;
import com.company.scopery.modules.traceability.usecase.application.query.ListUseCaseMentionOptionsQuery;
import com.company.scopery.modules.traceability.usecase.application.response.PrimaryFunctionChangeImpactResponse;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseFlowScopeResponse;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseMentionOptionsResponse;
import com.company.scopery.modules.traceability.usecase.application.service.UseCaseFlowScopeQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Tag(name = "Traceability - Use Case Flow Scope")
public class UseCaseFlowScopeController {

    private final UseCaseFlowScopeQueryService service;

    public UseCaseFlowScopeController(UseCaseFlowScopeQueryService service) {
        this.service = service;
    }

    @GetMapping(TraceabilityApiPaths.USE_CASE_FLOW_SCOPE)
    @Operation(summary = "Function-scoped screens/APIs/entities for Use Case Flow editor (no components)")
    public ApiResponse<UseCaseFlowScopeResponse> flowScope(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId) {
        return ApiResponse.success(service.getFlowScope(new GetUseCaseFlowScopeQuery(projectId, useCaseId)));
    }

    @GetMapping(TraceabilityApiPaths.USE_CASE_MENTION_OPTIONS)
    @Operation(summary = "Scoped @mention candidates for Use Case Flow steps")
    public ApiResponse<UseCaseMentionOptionsResponse> mentionOptions(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String types,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) UUID screenId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String mode) {
        String typesCsv = types != null ? types : type;
        return ApiResponse.success(service.listMentionOptions(new ListUseCaseMentionOptionsQuery(
                projectId, useCaseId, query, typesCsv, screenId, limit, mode)));
    }

    @GetMapping(TraceabilityApiPaths.USE_CASE_PRIMARY_FN_IMPACT)
    @Operation(summary = "Preview mentions that would be out of scope if parent Function changes")
    public ApiResponse<PrimaryFunctionChangeImpactResponse> primaryFunctionChangeImpact(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @RequestParam UUID newFunctionId) {
        return ApiResponse.success(service.primaryFunctionChangeImpact(
                new GetPrimaryFunctionChangeImpactQuery(projectId, useCaseId, newFunctionId)));
    }
}
