package com.company.scopery.modules.trust.privacy.application.action;
import com.company.scopery.modules.trust.privacy.application.response.PrivacyRequestResponse;
import com.company.scopery.modules.trust.privacy.domain.model.PrivacyRequestRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CancelPrivacyRequestAction {
    private final PrivacyRequestRepository repo;
    private final TrustAuthorizationService auth;
    public CancelPrivacyRequestAction(PrivacyRequestRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional
    public PrivacyRequestResponse execute(UUID workspaceId, UUID requestId) {
        auth.requireManage(workspaceId);
        var req = repo.findById(requestId).orElseThrow(() -> TrustExceptions.privacyNotFound(requestId));
        try { return PrivacyRequestResponse.from(repo.save(req.cancel())); }
        catch (IllegalStateException e) { throw TrustExceptions.privacyInvalidStatus(); }
    }
}
