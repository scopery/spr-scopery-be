package com.company.scopery.modules.governance.policy.application.response;
import com.company.scopery.modules.governance.policy.domain.model.GovernancePolicy;
import java.util.UUID;
public record GovernancePolicyResponse(UUID id, String objectTypeCode, String versioningMode, boolean versionOnUpdate, boolean lockOnFinalize,
        boolean allowRestore, String baselineGuardMode, String status) {
    public static GovernancePolicyResponse from(GovernancePolicy p) {
        return new GovernancePolicyResponse(p.id(), p.objectTypeCode(), p.versioningMode(), p.versionOnUpdate(), p.lockOnFinalize(),
                p.allowRestore(), p.baselineGuardMode(), p.status());
    }
}
