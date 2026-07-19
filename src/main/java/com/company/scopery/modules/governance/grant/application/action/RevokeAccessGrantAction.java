package com.company.scopery.modules.governance.grant.application.action;
import com.company.scopery.modules.governance.grant.application.command.RevokeAccessGrantCommand;
import com.company.scopery.modules.governance.grant.application.response.ObjectAccessGrantResponse;
import com.company.scopery.modules.governance.grant.domain.model.*;
import com.company.scopery.modules.governance.shared.authorization.GovernanceAuthorizationService;
import com.company.scopery.modules.governance.shared.error.GovernanceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class RevokeAccessGrantAction {
    private final ObjectAccessGrantRepository grants; private final GovernanceAuthorizationService authorization;
    public RevokeAccessGrantAction(ObjectAccessGrantRepository grants, GovernanceAuthorizationService authorization) { this.grants=grants; this.authorization=authorization; }
    @Transactional
    public ObjectAccessGrantResponse execute(RevokeAccessGrantCommand c) {
        authorization.requireOwnershipAssign(c.projectId());
        var g = grants.findById(c.grantId()).orElseThrow(() -> GovernanceExceptions.grantNotFound(c.grantId()));
        return ObjectAccessGrantResponse.from(grants.save(g.revoke()));
    }
}
