package com.company.scopery.modules.projectbaseline.baseline.application.action;

import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectbaseline.baseline.application.response.ProjectBaselineResponse;
import com.company.scopery.modules.projectbaseline.baseline.domain.enums.BaselineStatus;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaseline;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaselineRepository;
import com.company.scopery.modules.projectbaseline.shared.activity.ProjectBaselineActivityLogger;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.constant.*;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import com.company.scopery.modules.projectbaseline.shared.support.ProjectBaselinePlatformPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class ValidateBaselineAction {
    private final ProjectRepository projects;
    private final ProjectBaselineRepository baselines;
    private final ProjectPhaseRepository phases;
    private final ObjectMapper objectMapper;
    private final ProjectBaselineAuthorizationService authorization;
    private final ProjectBaselinePlatformPublisher publisher;
    private final ProjectBaselineActivityLogger activityLogger;

    public ValidateBaselineAction(ProjectRepository projects, ProjectBaselineRepository baselines,
                                  ProjectPhaseRepository phases, ObjectMapper objectMapper,
                                  ProjectBaselineAuthorizationService authorization,
                                  ProjectBaselinePlatformPublisher publisher,
                                  ProjectBaselineActivityLogger activityLogger) {
        this.projects = projects; this.baselines = baselines; this.phases = phases;
        this.objectMapper = objectMapper; this.authorization = authorization;
        this.publisher = publisher; this.activityLogger = activityLogger;
    }

    @Transactional
    public ProjectBaselineResponse execute(java.util.UUID projectId, java.util.UUID baselineId) {
        authorization.requireBaselineValidate(projectId);
        projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        ProjectBaseline baseline = baselines.findByIdAndProjectId(baselineId, projectId)
                .orElseThrow(() -> ProjectBaselineExceptions.baselineNotFound(baselineId));
        if (baseline.status() != BaselineStatus.DRAFT && baseline.status() != BaselineStatus.READY) {
            throw ProjectBaselineExceptions.baselineInvalidStatus(baseline.id(), "validate");
        }
        List<String> errors = new ArrayList<>();
        if (phases.findAllByProjectId(projectId).isEmpty()) errors.add("Project must have at least one phase");
        if (baseline.sourceEstimationRunId() == null) errors.add("Estimation run is required");
        if (baseline.snapshotJson() == null || baseline.snapshotJson().isBlank()) errors.add("Snapshot is required");
        try {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("valid", errors.isEmpty());
            result.put("errors", errors);
            String validationJson = objectMapper.writeValueAsString(result);
            if (!errors.isEmpty()) {
                baseline = baselines.save(baseline.withValidation(validationJson));
                throw ProjectBaselineExceptions.validationFailed(String.join("; ", errors));
            }
            baseline = baselines.save(baseline.markReady(validationJson));
        } catch (com.company.scopery.common.exception.AppException e) {
            throw e;
        } catch (Exception e) {
            throw ProjectBaselineExceptions.validationFailed(e.getMessage());
        }
        publisher.enqueueBaseline(baseline, ProjectBaselineEventCodes.PROJECT_BASELINE_VALIDATED);
        activityLogger.logSuccess(ProjectBaselineEntityTypes.PROJECT_BASELINE, baseline.id(),
                ProjectBaselineActivityActions.PROJECT_BASELINE_VALIDATED, "PROJECT_BASELINE_VALIDATED");
        return ProjectBaselineResponse.from(baseline);
    }
}
