package com.company.scopery.modules.trust.consent.application.action;
import com.company.scopery.modules.trust.consent.application.response.ConsentRecordResponse;
import com.company.scopery.modules.trust.consent.domain.model.ConsentRecordRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class WithdrawConsentRecordAction {
    private final ConsentRecordRepository repo;
    private final TrustAuthorizationService auth;
    public WithdrawConsentRecordAction(ConsentRecordRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional
    public ConsentRecordResponse execute(UUID workspaceId, UUID consentId) {
        auth.requireManage(workspaceId);
        var r = repo.findById(consentId).orElseThrow(() -> TrustExceptions.legalHoldNotFound(consentId));
        try { return ConsentRecordResponse.from(repo.save(r.withdraw())); }
        catch (IllegalStateException e) { throw TrustExceptions.privacyInvalidStatus(); }
    }
}
