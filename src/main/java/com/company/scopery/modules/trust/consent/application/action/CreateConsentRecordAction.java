package com.company.scopery.modules.trust.consent.application.action;
import com.company.scopery.modules.trust.consent.application.response.ConsentRecordResponse;
import com.company.scopery.modules.trust.consent.domain.model.ConsentRecord;
import com.company.scopery.modules.trust.consent.domain.model.ConsentRecordRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateConsentRecordAction {
    private final ConsentRecordRepository repo;
    private final TrustAuthorizationService auth;
    public CreateConsentRecordAction(ConsentRecordRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional
    public ConsentRecordResponse execute(UUID workspaceId, String consentType) {
        auth.requireManage(workspaceId);
        return ConsentRecordResponse.from(repo.save(ConsentRecord.given(workspaceId, consentType)));
    }
}
