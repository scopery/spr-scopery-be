package com.company.scopery.modules.resourcecapacity.actualeffort.application.action;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.resourcecapacity.actualeffort.application.response.ActualEffortRecordResponse;
import com.company.scopery.modules.resourcecapacity.actualeffort.domain.enums.ActualEffortInputMode;
import com.company.scopery.modules.resourcecapacity.actualeffort.domain.model.*;
import com.company.scopery.modules.resourcecapacity.actualeffort.http.request.CreateActualEffortRequest;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.*;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.util.CapacityEnumParser;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class RecordActualEffortAction {
    private final ActualEffortRecordRepository repo; private final ProjectRepository projects;
    private final CapacityWorkspaceAuthorizationService auth; private final CapacityActivityLogger activity;
    public RecordActualEffortAction(ActualEffortRecordRepository repo, ProjectRepository projects,
                                    CapacityWorkspaceAuthorizationService auth, CapacityActivityLogger activity) {
        this.repo=repo; this.projects=projects; this.auth=auth; this.activity=activity;
    }
    @Transactional
    public ActualEffortRecordResponse execute(UUID projectId, CreateActualEffortRequest r) {
        var project = projects.findById(projectId).orElseThrow(() -> CapacityExceptions.allocationProjectNotFound(projectId));
        auth.requireWorkspacePermission(project.workspaceId(), IamAuthorities.CAPACITY_VIEW);
        if (r.effortHours() == null || r.effortHours().signum() < 0) throw CapacityExceptions.actualEffortInvalidHours();
        var mode = CapacityEnumParser.parseRequired(ActualEffortInputMode.class, r.inputMode(), "ACTUAL_EFFORT_INVALID_MODE", "inputMode");
        try {
            var saved = repo.save(ActualEffortRecord.record(project.workspaceId(), projectId, r.resourceProfileId(),
                    r.targetType(), r.targetId(), r.effortDate(), r.effortHours(), mode, r.description()));
            activity.logSuccess(CapacityEntityTypes.ACTUAL_EFFORT_RECORD, saved.id(), CapacityActivityActions.ACTUAL_EFFORT_RECORDED, "Actual effort recorded");
            return ActualEffortRecordResponse.from(saved);
        } catch (IllegalArgumentException ex) { throw CapacityExceptions.actualEffortInvalidHours(); }
    }
}
