package com.company.scopery.modules.project.timeline.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.timeline.application.command.UpsertProgressSnapshotCommand;
import com.company.scopery.modules.project.timeline.application.response.TaskProgressSnapshotResponse;
import com.company.scopery.modules.project.timeline.domain.model.TaskProgressSnapshot;
import com.company.scopery.modules.project.timeline.domain.model.TaskProgressSnapshotRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class UpsertProgressSnapshotAction {

    private final ProjectWorkspaceAuthorizationService authorization;
    private final ProjectMutationGuard mutationGuard;
    private final TaskRepository tasks;
    private final TaskProgressSnapshotRepository snapshots;
    private final CurrentUserAuthorizationService currentUser;

    public UpsertProgressSnapshotAction(
            ProjectWorkspaceAuthorizationService authorization,
            ProjectMutationGuard mutationGuard,
            TaskRepository tasks,
            TaskProgressSnapshotRepository snapshots,
            CurrentUserAuthorizationService currentUser) {
        this.authorization = authorization;
        this.mutationGuard = mutationGuard;
        this.tasks = tasks;
        this.snapshots = snapshots;
        this.currentUser = currentUser;
    }

    @Transactional
    public TaskProgressSnapshotResponse execute(UpsertProgressSnapshotCommand cmd) {
        authorization.requireTaskUpdate(cmd.projectId());
        mutationGuard.requireMutableProject(cmd.projectId());

        Task task = tasks.findById(cmd.taskId())
                .orElseThrow(() -> ProjectExceptions.taskNotFound(cmd.taskId()));
        if (!task.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.taskProjectMismatch(cmd.taskId(), cmd.projectId());
        }

        if (cmd.progressPercent() == null
                || cmd.progressPercent().compareTo(BigDecimal.ZERO) < 0
                || cmd.progressPercent().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw ProjectExceptions.timelineInvalidProgressPercent();
        }

        LocalDate snapshotDate = cmd.snapshotDate() != null ? cmd.snapshotDate() : LocalDate.now();
        UUID actorId = currentUser.resolveCurrentUser().id();

        TaskProgressSnapshot saved = snapshots
                .findByTaskIdAndSnapshotDate(cmd.taskId(), snapshotDate)
                .map(existing -> existing.withProgress(
                        cmd.progressPercent(),
                        cmd.timeSpentMinutes(),
                        cmd.note(),
                        actorId))
                .orElseGet(() -> TaskProgressSnapshot.create(
                        cmd.projectId(),
                        cmd.taskId(),
                        snapshotDate,
                        cmd.progressPercent(),
                        cmd.timeSpentMinutes(),
                        cmd.note(),
                        actorId));

        TaskProgressSnapshot persisted = snapshots.save(saved);
        return new TaskProgressSnapshotResponse(
                persisted.id(),
                persisted.projectId(),
                persisted.taskId(),
                persisted.snapshotDate(),
                persisted.progressPercent(),
                persisted.timeSpentMinutes(),
                persisted.note(),
                persisted.recordedBy(),
                persisted.recordedAt());
    }
}
