package com.company.scopery.modules.clientportal.shared.security;
import com.company.scopery.modules.clientportal.grant.domain.model.ExternalProjectAccessGrantRepository;
import com.company.scopery.modules.clientportal.shared.error.ClientPortalExceptions;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class PortalGrantEnforcementService {
    private final ExternalProjectAccessGrantRepository grants;
    private final CurrentPortalAccountService currentPortalAccount;
    public PortalGrantEnforcementService(ExternalProjectAccessGrantRepository grants, CurrentPortalAccountService currentPortalAccount) {
        this.grants = grants;
        this.currentPortalAccount = currentPortalAccount;
    }
    public void requireActiveGrant(UUID projectId) {
        UUID accountId = currentPortalAccount.requireCurrentPortalAccountId();
        var grant = grants.findByProjectIdAndPortalAccountId(projectId, accountId)
                .orElseThrow(ClientPortalExceptions::accessDenied);
        if (!grant.isEffective()) throw ClientPortalExceptions.accessDenied();
    }
}
