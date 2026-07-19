package com.company.scopery.modules.trust.privacy.application.action;
import com.company.scopery.modules.trust.privacy.application.response.PrivacyRequestResponse;
import com.company.scopery.modules.trust.privacy.domain.model.PrivacyRequestRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class RejectPrivacyRequestAction {
    private final PrivacyRequestRepository repo; private final TrustAuthorizationService auth;
    public RejectPrivacyRequestAction(PrivacyRequestRepository repo, TrustAuthorizationService auth){this.repo=repo;this.auth=auth;}
    @Transactional
    public PrivacyRequestResponse execute(UUID workspaceId, UUID requestId, String reason) {
        auth.requireManage(workspaceId);
        if (reason == null || reason.isBlank()) throw TrustExceptions.rejectionReasonRequired();
        var req = repo.findById(requestId).orElseThrow(() -> TrustExceptions.privacyNotFound(requestId));
        if (!req.workspaceId().equals(workspaceId)) throw TrustExceptions.privacyNotFound(requestId);
        try { return PrivacyRequestResponse.from(repo.save(req.reject(reason))); }
        catch (IllegalArgumentException ex) { throw TrustExceptions.rejectionReasonRequired(); }
    }
}
