package com.company.scopery.modules.trust.retention.application.action;
import com.company.scopery.modules.trust.retention.application.response.RetentionJobResponse;
import com.company.scopery.modules.trust.retention.application.service.RetentionDryRunService;
import com.company.scopery.modules.trust.retention.domain.model.*;
import com.company.scopery.modules.trust.shared.activity.TrustActivityLogger;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.constant.*;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class RunRetentionDryRunAction {
    private final RetentionPolicyRepository policies; private final RetentionJobRepository jobs;
    private final RetentionDryRunService dryRun; private final TrustAuthorizationService auth; private final TrustActivityLogger activity;
    public RunRetentionDryRunAction(RetentionPolicyRepository policies, RetentionJobRepository jobs, RetentionDryRunService dryRun,
                                    TrustAuthorizationService auth, TrustActivityLogger activity) {
        this.policies=policies; this.jobs=jobs; this.dryRun=dryRun; this.auth=auth; this.activity=activity;
    }
    @Transactional
    public RetentionJobResponse execute(UUID workspaceId, UUID policyId, long simulatedCandidates) {
        auth.requireManage(workspaceId);
        var policy = policies.findById(policyId).orElseThrow(TrustExceptions::retentionPolicyNotFound);
        if (!workspaceId.equals(policy.workspaceId())) throw TrustExceptions.retentionPolicyNotFound();
        var result = dryRun.runDryRun(workspaceId, policy, simulatedCandidates);
        var saved = jobs.save(result);
        activity.logSuccess(TrustEntityTypes.RETENTION_JOB, saved.id(), TrustActivityActions.RETENTION_JOB_COMPLETED, "Retention dry-run completed");
        return RetentionJobResponse.from(saved);
    }
}
