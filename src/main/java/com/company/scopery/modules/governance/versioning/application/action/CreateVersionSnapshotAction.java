package com.company.scopery.modules.governance.versioning.application.action;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.governance.shared.activity.GovernanceActivityLogger;
import com.company.scopery.modules.governance.shared.authorization.GovernanceAuthorizationService;
import com.company.scopery.modules.governance.versioning.application.command.CreateVersionSnapshotCommand;
import com.company.scopery.modules.governance.versioning.application.response.GovernanceVersionRecordResponse;
import com.company.scopery.modules.governance.versioning.domain.model.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateVersionSnapshotAction {
    private final ProjectRepository projects; private final GovernanceSnapshotRepository snapshots;
    private final GovernanceVersionRecordRepository versions; private final GovernanceAuthorizationService authorization;
    private final GovernanceActivityLogger activityLogger;
    public CreateVersionSnapshotAction(ProjectRepository projects, GovernanceSnapshotRepository snapshots, GovernanceVersionRecordRepository versions,
                                       GovernanceAuthorizationService authorization, GovernanceActivityLogger activityLogger) {
        this.projects=projects; this.snapshots=snapshots; this.versions=versions; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public GovernanceVersionRecordResponse execute(CreateVersionSnapshotCommand c) {
        authorization.requireLockCreate(c.projectId());
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        versions.findCurrent(c.objectTypeCode(), c.targetId()).ifPresent(cur -> versions.save(cur.clearCurrent()));
        int next = versions.findByTarget(c.objectTypeCode(), c.targetId()).stream().mapToInt(GovernanceVersionRecord::versionNumber).max().orElse(0) + 1;
        var snap = snapshots.save(GovernanceSnapshot.create(project.workspaceId(), c.objectTypeCode(), c.targetId(), c.snapshotJson()));
        var rec = versions.save(GovernanceVersionRecord.create(project.workspaceId(), project.id(), c.objectTypeCode(), c.targetId(), snap.id(), "UPDATE", c.changeReason(), next));
        activityLogger.logSuccess("OBJECT_VERSION", rec.id(), "OBJECT_VERSION_CREATED", "Version snapshot created");
        return GovernanceVersionRecordResponse.from(rec);
    }
}
