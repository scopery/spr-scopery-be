package com.company.scopery.modules.servicesupport.supportcase.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import com.company.scopery.modules.servicesupport.supportcase.application.action.*;
import com.company.scopery.modules.servicesupport.supportcase.application.command.*;
import com.company.scopery.modules.servicesupport.supportcase.application.response.SupportCaseResponse;
import com.company.scopery.modules.servicesupport.supportcase.application.service.SupportCaseQueryService;
import com.company.scopery.modules.servicesupport.supportcase.http.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - Cases")
public class SupportCaseController {
    private final SupportCaseQueryService query;
    private final CreateSupportCaseAction createAction;
    private final TriageSupportCaseAction triageAction;
    private final ResolveSupportCaseAction resolveAction;
    private final CloseSupportCaseAction closeAction;

    public SupportCaseController(SupportCaseQueryService query, CreateSupportCaseAction createAction,
            TriageSupportCaseAction triageAction, ResolveSupportCaseAction resolveAction,
            CloseSupportCaseAction closeAction) {
        this.query = query; this.createAction = createAction; this.triageAction = triageAction;
        this.resolveAction = resolveAction; this.closeAction = closeAction;
    }

    @GetMapping(SupportApiPaths.CASES)
    @Operation(summary = "List support cases in workspace")
    public ApiResponse<List<SupportCaseResponse>> list(@PathVariable UUID workspaceId,
            @RequestParam(defaultValue = "false") boolean portalView) {
        return ApiResponse.success(query.listByWorkspace(workspaceId, portalView));
    }

    @PostMapping(SupportApiPaths.CASES)
    @Operation(summary = "Create support case")
    public ApiResponse<SupportCaseResponse> create(@PathVariable UUID workspaceId,
            @RequestBody @Valid CreateSupportCaseRequest req) {
        return ApiResponse.success(createAction.execute(workspaceId,
                new CreateSupportCaseCommand(req.title(), req.requestTypeCode(), req.priority(),
                        req.projectId(), req.source(), Boolean.TRUE.equals(req.portalVisible()))));
    }

    @PostMapping(SupportApiPaths.CASE_TRIAGE)
    @Operation(summary = "Triage support case")
    public ApiResponse<SupportCaseResponse> triage(@PathVariable UUID workspaceId,
            @PathVariable UUID caseId, @RequestBody @Valid TriageSupportCaseRequest req) {
        return ApiResponse.success(triageAction.execute(workspaceId, caseId,
                new TriageSupportCaseCommand(req.ownerUserId(), req.slaPolicyId())));
    }

    @PostMapping(SupportApiPaths.CASE_RESOLVE)
    @Operation(summary = "Resolve support case")
    public ApiResponse<SupportCaseResponse> resolve(@PathVariable UUID workspaceId,
            @PathVariable UUID caseId) {
        return ApiResponse.success(resolveAction.execute(workspaceId, caseId));
    }

    @PostMapping(SupportApiPaths.CASE_CLOSE)
    @Operation(summary = "Close support case")
    public ApiResponse<SupportCaseResponse> close(@PathVariable UUID workspaceId,
            @PathVariable UUID caseId) {
        return ApiResponse.success(closeAction.execute(workspaceId, caseId));
    }
}
