package com.company.scopery.modules.trust.retention.application.service;
import com.company.scopery.modules.trust.legalhold.domain.model.LegalHoldRepository;
import com.company.scopery.modules.trust.retention.domain.model.RetentionJob;
import com.company.scopery.modules.trust.retention.domain.model.RetentionPolicy;
import org.springframework.stereotype.Service;
import java.util.UUID;
@Service
public class RetentionDryRunService {
    private final LegalHoldRepository legalHolds;
    public RetentionDryRunService(LegalHoldRepository legalHolds){this.legalHolds=legalHolds;}
    public RetentionJob runDryRun(UUID workspaceId, RetentionPolicy policy, long simulatedCandidates) {
        long skipped = legalHolds.findActiveByWorkspaceId(workspaceId).isEmpty() ? 0 : simulatedCandidates;
        // dry-run never actioned rows; legal-hold candidates are counted as skipped only
        return RetentionJob.dryRun(workspaceId, policy.id()).complete(simulatedCandidates, 0, skipped);
    }
}
