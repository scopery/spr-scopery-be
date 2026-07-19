package com.company.scopery.modules.projectbaseline.baseline.application.service;

import com.company.scopery.modules.projectbaseline.baseline.application.response.ProjectBaselineResponse;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaselineRepository;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BaselineQueryService {
    private final ProjectBaselineRepository baselines;
    private final ProjectBaselineAuthorizationService authorization;

    public BaselineQueryService(ProjectBaselineRepository baselines,
                                ProjectBaselineAuthorizationService authorization) {
        this.baselines = baselines;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<ProjectBaselineResponse> list(UUID projectId) {
        authorization.requireBaselineView(projectId);
        return baselines.findByProjectId(projectId).stream().map(ProjectBaselineResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProjectBaselineResponse get(UUID projectId, UUID baselineId) {
        authorization.requireBaselineView(projectId);
        return baselines.findByIdAndProjectId(baselineId, projectId)
                .map(ProjectBaselineResponse::from)
                .orElseThrow(() -> ProjectBaselineExceptions.baselineNotFound(baselineId));
    }

    @Transactional(readOnly = true)
    public ProjectBaselineResponse getCurrent(UUID projectId) {
        authorization.requireBaselineView(projectId);
        return baselines.findCurrentByProjectId(projectId)
                .map(ProjectBaselineResponse::from)
                .orElseThrow(() -> ProjectBaselineExceptions.baselineNotFound(null));
    }
}
