package com.company.scopery.modules.trust.legalhold.application.action;
import com.company.scopery.modules.trust.legalhold.application.response.LegalHoldResponse;
import com.company.scopery.modules.trust.legalhold.domain.model.LegalHold;
import com.company.scopery.modules.trust.legalhold.domain.model.LegalHoldRepository;
import com.company.scopery.modules.trust.legalhold.http.request.CreateLegalHoldRequest;
import com.company.scopery.modules.trust.shared.activity.TrustActivityLogger;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.constant.*;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateLegalHoldAction {
    private final LegalHoldRepository repo; private final TrustAuthorizationService auth; private final TrustActivityLogger activity;
    public CreateLegalHoldAction(LegalHoldRepository repo, TrustAuthorizationService auth, TrustActivityLogger activity){this.repo=repo;this.auth=auth;this.activity=activity;}
    @Transactional
    public LegalHoldResponse execute(UUID workspaceId, CreateLegalHoldRequest r) {
        auth.requireManage(workspaceId);
        try {
            var saved = repo.save(LegalHold.create(workspaceId, r.holdType(), r.scopeType(), r.scopeId(), r.reason()));
            activity.logSuccess(TrustEntityTypes.LEGAL_HOLD, saved.id(), TrustActivityActions.LEGAL_HOLD_CREATED, "Legal hold created");
            return LegalHoldResponse.from(saved);
        } catch (IllegalArgumentException ex) { throw TrustExceptions.legalHoldReasonRequired(); }
    }
}
