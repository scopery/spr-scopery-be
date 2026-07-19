package com.company.scopery.modules.trust.legalhold.application.action;
import com.company.scopery.modules.trust.legalhold.application.response.LegalHoldResponse;
import com.company.scopery.modules.trust.legalhold.domain.model.LegalHoldRepository;
import com.company.scopery.modules.trust.shared.activity.TrustActivityLogger;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.constant.*;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ReleaseLegalHoldAction {
    private final LegalHoldRepository repo; private final TrustAuthorizationService auth; private final TrustActivityLogger activity;
    public ReleaseLegalHoldAction(LegalHoldRepository repo, TrustAuthorizationService auth, TrustActivityLogger activity){this.repo=repo;this.auth=auth;this.activity=activity;}
    @Transactional
    public LegalHoldResponse execute(UUID workspaceId, UUID holdId, String releaseReason) {
        auth.requireManage(workspaceId);
        if (releaseReason == null || releaseReason.isBlank()) throw TrustExceptions.releaseReasonRequired();
        var hold = repo.findById(holdId).orElseThrow(() -> TrustExceptions.legalHoldNotFound(holdId));
        if (!hold.workspaceId().equals(workspaceId)) throw TrustExceptions.legalHoldNotFound(holdId);
        try {
            var saved = repo.save(hold.release(releaseReason));
            activity.logSuccess(TrustEntityTypes.LEGAL_HOLD, saved.id(), TrustActivityActions.LEGAL_HOLD_RELEASED, "Released");
            return LegalHoldResponse.from(saved);
        } catch (IllegalArgumentException ex) { throw TrustExceptions.releaseReasonRequired(); }
    }
}
