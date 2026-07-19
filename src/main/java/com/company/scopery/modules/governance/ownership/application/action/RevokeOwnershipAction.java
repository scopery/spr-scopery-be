package com.company.scopery.modules.governance.ownership.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.governance.ownership.application.command.RevokeOwnershipCommand;
import com.company.scopery.modules.governance.ownership.application.response.ObjectOwnershipResponse;
import com.company.scopery.modules.governance.ownership.domain.model.*;
import com.company.scopery.modules.governance.shared.authorization.GovernanceAuthorizationService;
import com.company.scopery.modules.governance.shared.error.GovernanceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class RevokeOwnershipAction {
    private final ObjectOwnershipRepository ownerships; private final GovernanceAuthorizationService authorization; private final CurrentUserAuthorizationService currentUser;
    public RevokeOwnershipAction(ObjectOwnershipRepository ownerships, GovernanceAuthorizationService authorization, CurrentUserAuthorizationService currentUser) {
        this.ownerships=ownerships; this.authorization=authorization; this.currentUser=currentUser;
    }
    @Transactional
    public ObjectOwnershipResponse execute(RevokeOwnershipCommand c) {
        authorization.requireOwnershipAssign(c.projectId());
        var o = ownerships.findActive(c.objectTypeCode(), c.targetId()).orElseThrow(() -> GovernanceExceptions.ownershipNotFound(c.objectTypeCode(), c.targetId()));
        return ObjectOwnershipResponse.from(ownerships.save(o.revoke(currentUser.resolveCurrentUser().id())));
    }
}
