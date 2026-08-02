package com.company.scopery.modules.project.timeline.application.action;

import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.timeline.application.command.ReplaceDailyAllocationsCommand;
import com.company.scopery.modules.project.timeline.application.response.TaskDailyAllocationListResponse;
import com.company.scopery.modules.project.timeline.application.response.TaskDailyAllocationResponse;
import com.company.scopery.modules.project.timeline.domain.enums.AllocationSource;
import com.company.scopery.modules.project.timeline.domain.model.TaskDailyAllocation;
import com.company.scopery.modules.project.timeline.domain.model.TaskDailyAllocationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ReplaceDailyAllocationsAction {

    private final ProjectWorkspaceAuthorizationService authorization;
    private final ProjectMutationGuard mutationGuard;
    private final TaskRepository tasks;
    private final TaskDailyAllocationRepository allocations;

    public ReplaceDailyAllocationsAction(
            ProjectWorkspaceAuthorizationService authorization,
            ProjectMutationGuard mutationGuard,
            TaskRepository tasks,
            TaskDailyAllocationRepository allocations) {
        this.authorization = authorization;
        this.mutationGuard = mutationGuard;
        this.tasks = tasks;
        this.allocations = allocations;
    }

    @Transactional
    public TaskDailyAllocationListResponse execute(ReplaceDailyAllocationsCommand cmd) {
        authorization.requireTaskUpdate(cmd.projectId());
        mutationGuard.requireMutableProject(cmd.projectId());

        Task task = tasks.findById(cmd.taskId())
                .orElseThrow(() -> ProjectExceptions.taskNotFound(cmd.taskId()));
        if (!task.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.taskProjectMismatch(cmd.taskId(), cmd.projectId());
        }

        if (cmd.items() == null) {
            throw ProjectExceptions.timelineInvalidAllocation();
        }

        Set<java.time.LocalDate> seen = new HashSet<>();
        List<TaskDailyAllocation> next = new ArrayList<>();
        for (ReplaceDailyAllocationsCommand.DailyAllocationItem item : cmd.items()) {
            if (item == null || item.workDate() == null || item.plannedMinutes() < 0) {
                throw ProjectExceptions.timelineInvalidAllocation();
            }
            if (!seen.add(item.workDate())) {
                throw ProjectExceptions.timelineInvalidAllocation();
            }
            if (item.plannedMinutes() == 0) {
                continue;
            }
            next.add(TaskDailyAllocation.create(
                    cmd.projectId(),
                    cmd.taskId(),
                    item.workDate(),
                    item.plannedMinutes(),
                    AllocationSource.MANUAL));
        }

        allocations.deleteManualByTaskId(cmd.taskId());
        List<TaskDailyAllocation> saved = next.isEmpty() ? List.of() : allocations.saveAll(next);

        return new TaskDailyAllocationListResponse(
                saved.stream()
                        .map(a -> new TaskDailyAllocationResponse(
                                a.id(), a.taskId(), a.workDate(), a.plannedMinutes(), a.source()))
                        .toList());
    }
}
