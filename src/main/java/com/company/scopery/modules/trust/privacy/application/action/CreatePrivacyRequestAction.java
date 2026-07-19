package com.company.scopery.modules.trust.privacy.application.action;
import com.company.scopery.modules.trust.privacy.application.response.PrivacyRequestResponse;
import com.company.scopery.modules.trust.privacy.domain.model.PrivacyRequest;
import com.company.scopery.modules.trust.privacy.domain.model.PrivacyRequestRepository;
import com.company.scopery.modules.trust.privacy.http.request.CreatePrivacyRequestRequest;
import com.company.scopery.modules.trust.shared.activity.TrustActivityLogger;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.constant.*;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreatePrivacyRequestAction {
    private final PrivacyRequestRepository repo; private final TrustAuthorizationService auth; private final TrustActivityLogger activity;
    public CreatePrivacyRequestAction(PrivacyRequestRepository repo, TrustAuthorizationService auth, TrustActivityLogger activity){this.repo=repo;this.auth=auth;this.activity=activity;}
    @Transactional
    public PrivacyRequestResponse execute(UUID workspaceId, CreatePrivacyRequestRequest r) {
        auth.requireManage(workspaceId);
        var saved = repo.save(PrivacyRequest.submit(workspaceId, r.requestType(), r.subjectReference(), r.assignedOwnerUserId()));
        activity.logSuccess(TrustEntityTypes.PRIVACY_REQUEST, saved.id(), TrustActivityActions.PRIVACY_REQUEST_CREATED, "Privacy request created");
        return PrivacyRequestResponse.from(saved);
    }
}
