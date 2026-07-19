package com.company.scopery.modules.trust.evidence.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.trust.evidence.application.action.CreateComplianceEvidenceAction;
import com.company.scopery.modules.trust.evidence.application.action.FinalizeComplianceEvidenceAction;
import com.company.scopery.modules.trust.evidence.application.response.ComplianceEvidenceResponse;
import com.company.scopery.modules.trust.evidence.application.service.ComplianceEvidenceQueryService;
import com.company.scopery.modules.trust.evidence.http.request.CreateComplianceEvidenceRequest;
import com.company.scopery.modules.trust.shared.constant.TrustApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Trust / Compliance")
public class ComplianceEvidenceController {
    private final CreateComplianceEvidenceAction createAction;
    private final FinalizeComplianceEvidenceAction finalizeAction;
    private final ComplianceEvidenceQueryService queryService;
    public ComplianceEvidenceController(CreateComplianceEvidenceAction createAction, FinalizeComplianceEvidenceAction finalizeAction, ComplianceEvidenceQueryService queryService) {
        this.createAction = createAction; this.finalizeAction = finalizeAction; this.queryService = queryService;
    }
    @PostMapping(TrustApiPaths.EVIDENCE_RECORDS) @Operation(summary = "Create compliance evidence")
    public ApiResponse<ComplianceEvidenceResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateComplianceEvidenceRequest r) {
        return ApiResponse.success(createAction.execute(workspaceId, r.evidenceType(), r.title(), null));
    }
    @GetMapping(TrustApiPaths.EVIDENCE_RECORDS) @Operation(summary = "List compliance evidence records")
    public ApiResponse<List<ComplianceEvidenceResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(queryService.listByWorkspace(workspaceId));
    }
    @GetMapping(TrustApiPaths.EVIDENCE_RECORDS + "/{evidenceId}") @Operation(summary = "Get compliance evidence record")
    public ApiResponse<ComplianceEvidenceResponse> getById(@PathVariable UUID workspaceId, @PathVariable UUID evidenceId) {
        return ApiResponse.success(queryService.getById(workspaceId, evidenceId));
    }
    @PostMapping(TrustApiPaths.EVIDENCE_RECORDS + "/{evidenceId}/finalize") @Operation(summary = "Finalize compliance evidence")
    public ApiResponse<ComplianceEvidenceResponse> finalize(@PathVariable UUID workspaceId, @PathVariable UUID evidenceId) {
        return ApiResponse.success(finalizeAction.execute(workspaceId, evidenceId, null));
    }
}
