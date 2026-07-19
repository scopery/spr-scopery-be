package com.company.scopery.modules.governance.versioning.application.action;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.governance.shared.authorization.GovernanceAuthorizationService;
import com.company.scopery.modules.governance.shared.error.GovernanceExceptions;
import com.company.scopery.modules.governance.versioning.application.command.RequestRestoreCommand;
import com.company.scopery.modules.governance.versioning.application.response.RestoreRequestResponse;
import com.company.scopery.modules.governance.versioning.domain.model.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class RequestRestoreAction {
    private final ProjectRepository projects; private final GovernanceVersionRecordRepository versions;
    private final RestoreRequestRepository restores; private final GovernanceAuthorizationService authorization;
    public RequestRestoreAction(ProjectRepository projects, GovernanceVersionRecordRepository versions, RestoreRequestRepository restores, GovernanceAuthorizationService authorization) {
        this.projects=projects; this.versions=versions; this.restores=restores; this.authorization=authorization;
    }
    @Transactional
    public RestoreRequestResponse execute(RequestRestoreCommand c) {
        authorization.requireLockCreate(c.projectId());
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        var from = versions.findById(c.restoreFromVersionRecordId()).orElseThrow(() -> GovernanceExceptions.versionNotFound(c.restoreFromVersionRecordId()));
        if (!from.restoreEligible()) throw GovernanceExceptions.restoreNotEligible(c.restoreFromVersionRecordId());
        var req = restores.save(RestoreRequest.request(project.workspaceId(), project.id(), c.objectTypeCode(), c.targetId(), c.restoreFromVersionRecordId(), c.reason()));
        return RestoreRequestResponse.from(restores.save(req.complete()));
    }
}
