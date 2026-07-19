package com.company.scopery.modules.governance.versioning.application.service;
import com.company.scopery.modules.governance.lock.domain.model.ObjectLockRepository;
import com.company.scopery.modules.governance.policy.domain.model.GovernancePolicyRepository;
import com.company.scopery.modules.governance.shared.error.GovernanceExceptions;
import com.company.scopery.modules.governance.versioning.domain.model.GovernanceVersionRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Service
public class BaselineGuardService {
    private final ObjectLockRepository locks; private final GovernanceVersionRecordRepository versions; private final GovernancePolicyRepository policies;
    public BaselineGuardService(ObjectLockRepository locks, GovernanceVersionRecordRepository versions, GovernancePolicyRepository policies) {
        this.locks=locks; this.versions=versions; this.policies=policies;
    }
    @Transactional(readOnly=true)
    public void assertChangeAllowed(UUID workspaceId, String objectType, UUID targetId, String proposedChangeType) {
        locks.findActive(objectType, targetId, "EDIT").ifPresent(l -> { throw GovernanceExceptions.lockActive(objectType, targetId); });
        locks.findActive(objectType, targetId, "FINALIZE").ifPresent(l -> { throw GovernanceExceptions.lockActive(objectType, targetId); });
        versions.findCurrent(objectType, targetId).ifPresent(v -> {
            if (v.finalizedFlag()) throw GovernanceExceptions.baselineGuardBlocked("Target is finalized");
        });
        policies.findByWorkspaceAndObjectType(workspaceId, objectType).ifPresent(p -> {
            if ("BLOCK".equalsIgnoreCase(p.baselineGuardMode()) && "MUTATE_BASELINE".equals(proposedChangeType)) {
                throw GovernanceExceptions.baselineGuardBlocked("Baseline guard mode BLOCK");
            }
        });
    }
}
