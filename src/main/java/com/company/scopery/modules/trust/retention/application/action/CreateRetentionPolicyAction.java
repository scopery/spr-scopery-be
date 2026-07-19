package com.company.scopery.modules.trust.retention.application.action;
import com.company.scopery.modules.trust.retention.application.response.RetentionPolicyResponse;
import com.company.scopery.modules.trust.retention.domain.model.*;
import com.company.scopery.modules.trust.shared.activity.TrustActivityLogger;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.constant.*;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateRetentionPolicyAction {
    private final RetentionPolicyRepository repo;
    private final TrustAuthorizationService auth;
    private final TrustActivityLogger activity;
    public CreateRetentionPolicyAction(RetentionPolicyRepository repo, TrustAuthorizationService auth, TrustActivityLogger activity) {
        this.repo = repo; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public RetentionPolicyResponse execute(UUID workspaceId, String policyCode, String name,
            String objectTypeCode, int retentionPeriodDays, String retentionAction) {
        auth.requireManage(workspaceId);
        var saved = repo.save(RetentionPolicy.create(workspaceId, policyCode, name, objectTypeCode, retentionPeriodDays, retentionAction));
        activity.logSuccess(TrustEntityTypes.RETENTION_POLICY, saved.id(), TrustActivityActions.RETENTION_POLICY_CREATED, "Retention policy created");
        return RetentionPolicyResponse.from(saved);
    }
}
