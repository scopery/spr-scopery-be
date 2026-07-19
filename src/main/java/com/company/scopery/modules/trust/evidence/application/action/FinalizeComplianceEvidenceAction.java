package com.company.scopery.modules.trust.evidence.application.action;
import com.company.scopery.modules.trust.evidence.application.response.ComplianceEvidenceResponse;
import com.company.scopery.modules.trust.evidence.domain.model.ComplianceEvidenceRecordRepository;
import com.company.scopery.modules.trust.shared.activity.TrustActivityLogger;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.constant.*;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class FinalizeComplianceEvidenceAction {
    private final ComplianceEvidenceRecordRepository repo; private final TrustAuthorizationService auth; private final TrustActivityLogger activity;
    public FinalizeComplianceEvidenceAction(ComplianceEvidenceRecordRepository repo, TrustAuthorizationService auth, TrustActivityLogger activity){this.repo=repo;this.auth=auth;this.activity=activity;}
    @Transactional
    public ComplianceEvidenceResponse execute(UUID workspaceId, UUID evidenceId, UUID actorId) {
        auth.requireManage(workspaceId);
        var e = repo.findById(evidenceId).orElseThrow(() -> TrustExceptions.evidenceNotFound(evidenceId));
        if (!e.workspaceId().equals(workspaceId)) throw TrustExceptions.evidenceNotFound(evidenceId);
        try {
            var saved = repo.save(e.finalizeEvidence(actorId));
            activity.logSuccess(TrustEntityTypes.COMPLIANCE_EVIDENCE, saved.id(), TrustActivityActions.COMPLIANCE_EVIDENCE_FINALIZED, "Finalized");
            return ComplianceEvidenceResponse.from(saved);
        } catch (IllegalStateException ex) { throw TrustExceptions.evidenceImmutable(); }
    }
}
