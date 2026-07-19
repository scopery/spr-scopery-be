package com.company.scopery.modules.project.scheduling.schedulerun.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.*;
import com.company.scopery.modules.project.scheduling.schedulerun.application.response.ScheduleRunResponse;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.*;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Component
public class CancelScheduleRunAction {
    private final ScheduleRunRepository runs; private final ProjectWorkspaceAuthorizationService authorization;
    private final ProjectRepository projects; private final CurrentUserAuthorizationService currentUser;
    private final ProjectPlatformPublisher publisher;
    public CancelScheduleRunAction(ScheduleRunRepository runs,ProjectWorkspaceAuthorizationService authorization,
            ProjectRepository projects,CurrentUserAuthorizationService currentUser,ProjectPlatformPublisher publisher){
        this.runs=runs;this.authorization=authorization;this.projects=projects;this.currentUser=currentUser;this.publisher=publisher;
    }
    @Transactional public ScheduleRunResponse execute(UUID projectId,UUID runId){
        authorization.requireScheduleCancel(projectId);
        ScheduleRun run=runs.findById(runId).filter(r->r.projectId().equals(projectId)).orElseThrow(()->ProjectExceptions.scheduleRunNotFound(runId));
        if(!run.cancellable())throw ProjectExceptions.scheduleRunNotCancellable(runId);
        ScheduleRun cancelled=runs.save(run.cancelled());
        Project project=projects.findById(projectId).orElseThrow(()->ProjectExceptions.projectNotFound(projectId));
        publisher.enqueueScheduleRun(cancelled,"SCHEDULE_RUN_CANCELLED");
        publisher.auditScheduleCancelled(currentUser.resolveCurrentUser().id(),project,cancelled);
        return ScheduleRunResponse.from(cancelled);
    }
}
