package com.company.scopery.modules.governance.ownership.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.governance.ownership.application.command.AssignOwnershipCommand;
import com.company.scopery.modules.governance.ownership.application.response.ObjectOwnershipResponse;
import com.company.scopery.modules.governance.ownership.domain.model.*;
import com.company.scopery.modules.governance.shared.activity.GovernanceActivityLogger;
import com.company.scopery.modules.governance.shared.authorization.GovernanceAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class AssignOwnershipAction {
    private final ProjectRepository projects; private final ObjectOwnershipRepository ownerships;
    private final GovernanceAuthorizationService authorization; private final CurrentUserAuthorizationService currentUser; private final GovernanceActivityLogger activityLogger;
    public AssignOwnershipAction(ProjectRepository projects, ObjectOwnershipRepository ownerships, GovernanceAuthorizationService authorization,
                                 CurrentUserAuthorizationService currentUser, GovernanceActivityLogger activityLogger) {
        this.projects=projects; this.ownerships=ownerships; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public ObjectOwnershipResponse execute(AssignOwnershipCommand c) {
        authorization.requireOwnershipAssign(c.projectId());
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        ownerships.findActive(c.objectTypeCode(), c.targetId()).ifPresent(existing -> ownerships.save(existing.revoke(currentUser.resolveCurrentUser().id())));
        var o = ownerships.save(ObjectOwnership.assign(project.workspaceId(), project.id(), c.objectTypeCode(), c.targetId(), c.ownerTargetType(), c.ownerTargetId(), currentUser.resolveCurrentUser().id()));
        activityLogger.logSuccess("OBJECT_OWNERSHIP", o.id(), "OBJECT_OWNER_ASSIGNED", "Owner assigned");
        return ObjectOwnershipResponse.from(o);
    }
}
