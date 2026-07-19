package com.company.scopery.modules.trust.evidence.application.service;
import com.company.scopery.modules.trust.evidence.application.response.ComplianceEvidenceResponse;
import com.company.scopery.modules.trust.evidence.domain.model.ComplianceEvidenceRecordRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ComplianceEvidenceQueryService {
    private final ComplianceEvidenceRecordRepository repo;
    private final TrustAuthorizationService auth;
    public ComplianceEvidenceQueryService(ComplianceEvidenceRecordRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<ComplianceEvidenceResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(ComplianceEvidenceResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public ComplianceEvidenceResponse getById(UUID workspaceId, UUID evidenceId) {
        auth.requireView(workspaceId);
        return repo.findById(evidenceId).map(ComplianceEvidenceResponse::from)
                .orElseThrow(() -> TrustExceptions.evidenceNotFound(evidenceId));
    }
}
