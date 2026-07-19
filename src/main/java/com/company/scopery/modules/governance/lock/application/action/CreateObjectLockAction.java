package com.company.scopery.modules.governance.lock.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.governance.lock.application.command.CreateObjectLockCommand;
import com.company.scopery.modules.governance.lock.application.response.ObjectLockResponse;
import com.company.scopery.modules.governance.lock.domain.model.*;
import com.company.scopery.modules.governance.shared.activity.GovernanceActivityLogger;
import com.company.scopery.modules.governance.shared.authorization.GovernanceAuthorizationService;
import com.company.scopery.modules.governance.shared.error.GovernanceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateObjectLockAction {
    private final ProjectRepository projects; private final ObjectLockRepository locks;
    private final GovernanceAuthorizationService authorization; private final CurrentUserAuthorizationService currentUser; private final GovernanceActivityLogger activityLogger;
    public CreateObjectLockAction(ProjectRepository projects, ObjectLockRepository locks, GovernanceAuthorizationService authorization,
                                  CurrentUserAuthorizationService currentUser, GovernanceActivityLogger activityLogger) {
        this.projects=projects; this.locks=locks; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public ObjectLockResponse execute(CreateObjectLockCommand c) {
        authorization.requireLockCreate(c.projectId());
        locks.findActive(c.objectTypeCode(), c.targetId(), c.lockType()).ifPresent(l -> { throw GovernanceExceptions.lockActive(c.objectTypeCode(), c.targetId()); });
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        var lock = locks.save(ObjectLock.create(project.workspaceId(), project.id(), c.objectTypeCode(), c.targetId(), c.lockType(), currentUser.resolveCurrentUser().id(), c.reason()));
        activityLogger.logSuccess("OBJECT_LOCK", lock.id(), "OBJECT_LOCK_CREATED", "Object locked");
        return ObjectLockResponse.from(lock);
    }
}
