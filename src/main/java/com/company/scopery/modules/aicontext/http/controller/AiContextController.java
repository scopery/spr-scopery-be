package com.company.scopery.modules.aicontext.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aicontext.audit.application.service.AiContextAuditQueryService;
import com.company.scopery.modules.aicontext.http.request.CreateAiContextPolicyRequest;
import com.company.scopery.modules.aicontext.http.request.ResolveAiContextRequest;
import com.company.scopery.modules.aicontext.policy.application.action.CreateAiContextPolicyAction;
import com.company.scopery.modules.aicontext.policy.application.command.CreateAiContextPolicyCommand;
import com.company.scopery.modules.aicontext.policy.application.response.AiContextPolicyResponse;
import com.company.scopery.modules.aicontext.resolution.application.action.ResolveAiContextAction;
import com.company.scopery.modules.aicontext.resolution.application.command.ResolveAiContextCommand;
import com.company.scopery.modules.aicontext.resolution.application.response.AiContextResolutionResult;
import com.company.scopery.modules.aicontext.shared.constant.AiContextApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = "AI Context")
public class AiContextController {

    private final ResolveAiContextAction resolveContext;
    private final CreateAiContextPolicyAction createPolicy;
    private final AiContextAuditQueryService auditQuery;

    public AiContextController(ResolveAiContextAction resolveContext,
                                CreateAiContextPolicyAction createPolicy,
                                AiContextAuditQueryService auditQuery) {
        this.resolveContext = resolveContext;
        this.createPolicy = createPolicy;
        this.auditQuery = auditQuery;
    }

    @PostMapping(AiContextApiPaths.RESOLVE)
    @Operation(summary = "Resolve AI context for a document")
    public ApiResponse<AiContextResolutionResult> resolve(@Valid @RequestBody ResolveAiContextRequest r) {
        return ApiResponse.success(resolveContext.execute(
                new ResolveAiContextCommand(r.policyId(), r.projectId(), r.documentId())));
    }

    @PostMapping(AiContextApiPaths.POLICIES)
    @Operation(summary = "Create an AI context resolution policy")
    public ApiResponse<AiContextPolicyResponse> createPolicy(@Valid @RequestBody CreateAiContextPolicyRequest r) {
        return ApiResponse.success(createPolicy.execute(new CreateAiContextPolicyCommand(
                r.workspaceId(), r.policyCode(), r.label(), r.maxTokens(), r.includeRelated())));
    }

    @GetMapping(AiContextApiPaths.AUDIT)
    @Operation(summary = "Get AI context resolution audit log for a document")
    public ApiResponse<PageResponse<AiContextAuditQueryService.AiContextAuditEntry>> getAudit(
            @RequestParam UUID documentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(auditQuery.listByDocument(documentId, page, size));
    }
}
