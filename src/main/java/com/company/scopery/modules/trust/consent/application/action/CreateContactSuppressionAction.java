package com.company.scopery.modules.trust.consent.application.action;
import com.company.scopery.modules.trust.consent.application.response.ContactSuppressionResponse;
import com.company.scopery.modules.trust.consent.domain.model.ContactSuppression;
import com.company.scopery.modules.trust.consent.domain.model.ContactSuppressionRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateContactSuppressionAction {
    private final ContactSuppressionRepository repo;
    private final TrustAuthorizationService auth;
    public CreateContactSuppressionAction(ContactSuppressionRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional
    public ContactSuppressionResponse execute(UUID workspaceId, UUID externalContactId, UUID portalAccountId,
            String suppressionType, String reason) {
        auth.requireManage(workspaceId);
        return ContactSuppressionResponse.from(repo.save(ContactSuppression.create(workspaceId,
                externalContactId, portalAccountId, suppressionType, reason)));
    }
}
