package com.company.scopery.modules.governance.grant.application.action;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.governance.grant.application.command.CreateAccessGrantCommand;
import com.company.scopery.modules.governance.grant.application.response.ObjectAccessGrantResponse;
import com.company.scopery.modules.governance.grant.domain.model.*;
import com.company.scopery.modules.governance.shared.activity.GovernanceActivityLogger;
import com.company.scopery.modules.governance.shared.authorization.GovernanceAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateAccessGrantAction {
    private final ProjectRepository projects; private final ObjectAccessGrantRepository grants;
    private final GovernanceAuthorizationService authorization; private final GovernanceActivityLogger activityLogger;
    public CreateAccessGrantAction(ProjectRepository projects, ObjectAccessGrantRepository grants, GovernanceAuthorizationService authorization, GovernanceActivityLogger activityLogger) {
        this.projects=projects; this.grants=grants; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public ObjectAccessGrantResponse execute(CreateAccessGrantCommand c) {
        authorization.requireOwnershipAssign(c.projectId());
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        var g = grants.save(ObjectAccessGrant.create(project.workspaceId(), project.id(), c.objectTypeCode(), c.targetId(), c.granteeType(), c.granteeId(), c.grantRole()));
        activityLogger.logSuccess("OBJECT_ACCESS_GRANT", g.id(), "OBJECT_ACCESS_GRANT_CREATED", "Access grant created");
        return ObjectAccessGrantResponse.from(g);
    }
}
