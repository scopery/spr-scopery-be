package com.company.scopery.modules.scope.evidence.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.scope.evidence.application.action.CreateAcceptanceEvidenceAction;
import com.company.scopery.modules.scope.evidence.application.command.CreateAcceptanceEvidenceCommand;
import com.company.scopery.modules.scope.evidence.application.response.AcceptanceEvidenceResponse;
import com.company.scopery.modules.scope.evidence.application.service.AcceptanceEvidenceQueryService;
import com.company.scopery.modules.scope.evidence.http.request.CreateAcceptanceEvidenceRequest;
import com.company.scopery.modules.scope.shared.constant.ScopeApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@Tag(name = "Scope - Evidence")
public class EvidenceController {
    private final CreateAcceptanceEvidenceAction create;
    private final AcceptanceEvidenceQueryService query;
    public EvidenceController(CreateAcceptanceEvidenceAction create, AcceptanceEvidenceQueryService query) {
        this.create = create; this.query = query;
    }
    @PostMapping(ScopeApiPaths.DELIVERABLES + "/{deliverableId}/evidence")
    @Operation(summary = "Create acceptance evidence for deliverable")
    public ApiResponse<AcceptanceEvidenceResponse> create(@PathVariable UUID projectId,
                                                          @PathVariable UUID deliverableId,
                                                          @Valid @RequestBody CreateAcceptanceEvidenceRequest request) {
        return ApiResponse.success(create.execute(new CreateAcceptanceEvidenceCommand(
                projectId, deliverableId, request.acceptanceCriteriaId(), request.evidenceType(),
                request.title(), request.contentText(), request.linkUrl(), request.referenceId())));
    }
    @GetMapping(ScopeApiPaths.DELIVERABLES + "/{deliverableId}/evidence")
    @Operation(summary = "List acceptance evidence for deliverable")
    public ApiResponse<List<AcceptanceEvidenceResponse>> list(@PathVariable UUID projectId,
                                                              @PathVariable UUID deliverableId) {
        return ApiResponse.success(query.listByDeliverable(projectId, deliverableId));
    }
    @GetMapping(ScopeApiPaths.EVIDENCE_BY_ID + "/{evidenceId}")
    @Operation(summary = "Get acceptance evidence by id")
    public ApiResponse<AcceptanceEvidenceResponse> get(@PathVariable UUID projectId, @PathVariable UUID evidenceId) {
        return ApiResponse.success(query.get(projectId, evidenceId));
    }
}
