package com.company.scopery.modules.trust.evidence.application.action;
import com.company.scopery.modules.trust.evidence.application.response.ComplianceEvidenceResponse;
import com.company.scopery.modules.trust.evidence.domain.model.ComplianceEvidenceRecord;
import com.company.scopery.modules.trust.evidence.domain.model.ComplianceEvidenceRecordRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateComplianceEvidenceAction {
    private final ComplianceEvidenceRecordRepository repo; private final TrustAuthorizationService auth;
    public CreateComplianceEvidenceAction(ComplianceEvidenceRecordRepository repo, TrustAuthorizationService auth){this.repo=repo;this.auth=auth;}
    @Transactional
    public ComplianceEvidenceResponse execute(UUID workspaceId, String type, String title, UUID ownerId) {
        auth.requireManage(workspaceId);
        return ComplianceEvidenceResponse.from(repo.save(ComplianceEvidenceRecord.create(workspaceId, type, title, ownerId)));
    }
}
