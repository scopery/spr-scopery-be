package com.company.scopery.modules.governance.lock.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.governance.lock.application.command.UnfinalizeObjectCommand;
import com.company.scopery.modules.governance.lock.application.response.ObjectLockResponse;
import com.company.scopery.modules.governance.lock.domain.model.*;
import com.company.scopery.modules.governance.shared.activity.GovernanceActivityLogger;
import com.company.scopery.modules.governance.shared.authorization.GovernanceAuthorizationService;
import com.company.scopery.modules.governance.shared.error.GovernanceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UnfinalizeObjectAction {
    private final ObjectLockRepository locks; private final GovernanceAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final GovernanceActivityLogger activityLogger;
    public UnfinalizeObjectAction(ObjectLockRepository locks, GovernanceAuthorizationService authorization,
                                  CurrentUserAuthorizationService currentUser, GovernanceActivityLogger activityLogger) {
        this.locks=locks; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public ObjectLockResponse execute(UnfinalizeObjectCommand c) {
        authorization.requireLockCreate(c.projectId());
        var lock = locks.findActive(c.objectTypeCode(), c.targetId(), "FINALIZED")
                .orElseThrow(() -> GovernanceExceptions.unfinalizeNotAllowed("No active FINALIZED lock for this object"));
        var released = locks.save(lock.release(currentUser.resolveCurrentUser().id()));
        activityLogger.logSuccess("OBJECT_LOCK", released.id(), "OBJECT_UNFINALIZED", "Object unfinalized");
        return ObjectLockResponse.from(released);
    }
}
