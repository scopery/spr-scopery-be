package com.company.scopery.modules.trust.consent.application.action;
import com.company.scopery.modules.trust.consent.application.response.ContactSuppressionResponse;
import com.company.scopery.modules.trust.consent.domain.model.ContactSuppressionRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ReleaseContactSuppressionAction {
    private final ContactSuppressionRepository repo;
    private final TrustAuthorizationService auth;
    public ReleaseContactSuppressionAction(ContactSuppressionRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional
    public ContactSuppressionResponse execute(UUID workspaceId, UUID suppressionId, String releaseReason) {
        auth.requireManage(workspaceId);
        var s = repo.findById(suppressionId).orElseThrow(() -> TrustExceptions.legalHoldNotFound(suppressionId));
        return ContactSuppressionResponse.from(repo.save(s.release(releaseReason)));
    }
}
