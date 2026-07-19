package com.company.scopery.modules.ratecard.resolution.application.action;

import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.ratecard.resolution.application.query.PreviewTaskRateQuery;
import com.company.scopery.modules.ratecard.resolution.application.query.ResolveRateQuery;
import com.company.scopery.modules.ratecard.resolution.application.response.RateSnapshotResponse;
import com.company.scopery.modules.ratecard.resolution.application.response.TaskRatePreviewResponse;
import com.company.scopery.modules.ratecard.resolution.application.service.RateResolutionService;
import com.company.scopery.modules.ratecard.resolution.domain.RateSnapshot;
import com.company.scopery.modules.ratecard.shared.authorization.RateCardAuthorizationService;
import com.company.scopery.modules.ratecard.shared.error.RateCardExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class PreviewTaskRateAction {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final RateResolutionService rateResolutionService;
    private final RateCardAuthorizationService authorizationService;

    public PreviewTaskRateAction(TaskRepository taskRepository,
                                 ProjectRepository projectRepository,
                                 RateResolutionService rateResolutionService,
                                 RateCardAuthorizationService authorizationService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.rateResolutionService = rateResolutionService;
        this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public TaskRatePreviewResponse execute(PreviewTaskRateQuery query) {
        Task task = taskRepository.findById(query.taskId())
                .orElseThrow(() -> RateCardExceptions.rateCardNotFound(query.taskId()));
        UUID workspaceId = query.workspaceId();
        if (workspaceId == null) {
            workspaceId = projectRepository.findById(task.projectId())
                    .orElseThrow(() -> RateCardExceptions.rateCardNotFound(task.projectId()))
                    .workspaceId();
        }
        authorizationService.requireResolutionPreview(workspaceId);

        UUID costRoleId = query.costRoleId();
        String costRoleCode = query.costRoleCode();
        if (costRoleId == null && (costRoleCode == null || costRoleCode.isBlank())) {
            costRoleCode = task.plannedRoleCode();
        }
        if (costRoleId == null && (costRoleCode == null || costRoleCode.isBlank())) {
            throw RateCardExceptions.roleNotResolved();
        }

        LocalDate targetDate = query.targetDate() != null ? query.targetDate() : LocalDate.now();
        RateSnapshot snapshot = rateResolutionService.resolve(new ResolveRateQuery(
                workspaceId, null, task.projectId(), costRoleId, costRoleCode,
                targetDate, query.currencyCode(), "COST"));

        BigDecimal hours = task.estimateHours() != null ? task.estimateHours() : BigDecimal.ZERO;
        BigDecimal preview = hours.multiply(snapshot.adjustedCostRate()).setScale(4, RoundingMode.HALF_UP);
        return new TaskRatePreviewResponse(
                task.id(), hours, RateSnapshotResponse.from(snapshot), preview, TaskRatePreviewResponse.LABEL);
    }
}
