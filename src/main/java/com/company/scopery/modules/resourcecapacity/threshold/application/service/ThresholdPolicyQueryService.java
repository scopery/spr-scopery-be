package com.company.scopery.modules.resourcecapacity.threshold.application.service;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.resourcecapacity.planning.application.response.ThresholdPolicyResponse;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.threshold.domain.model.UtilizationThresholdPolicy;
import com.company.scopery.modules.resourcecapacity.threshold.domain.model.UtilizationThresholdPolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service("resourceCapacityThresholdPolicyQueryService")
public class ThresholdPolicyQueryService {
    private final UtilizationThresholdPolicyRepository repo;
    private final ProjectRepository projects;
    private final CapacityWorkspaceAuthorizationService auth;

    public ThresholdPolicyQueryService(UtilizationThresholdPolicyRepository repo,
                                       ProjectRepository projects,
                                       CapacityWorkspaceAuthorizationService auth) {
        this.repo = repo;
        this.projects = projects;
        this.auth = auth;
    }

    @Transactional(readOnly = true)
    public ThresholdPolicyResponse getForWorkspace(UUID workspaceId) {
        auth.requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_VIEW);
        var policy = repo.findByWorkspaceIdAndProjectIdIsNull(workspaceId)
                .orElseGet(() -> UtilizationThresholdPolicy.defaults(workspaceId, null));
        return ThresholdPolicyResponse.from(policy);
    }

    @Transactional(readOnly = true)
    public ThresholdPolicyResponse getForProject(UUID projectId) {
        var project = projects.findById(projectId)
                .orElseThrow(() -> CapacityExceptions.allocationProjectNotFound(projectId));
        UUID workspaceId = project.workspaceId();
        auth.requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_VIEW);
        var policy = repo.findByProjectId(projectId)
                .or(() -> repo.findByWorkspaceIdAndProjectIdIsNull(workspaceId))
                .orElseGet(() -> UtilizationThresholdPolicy.defaults(workspaceId, projectId));
        return ThresholdPolicyResponse.from(policy);
    }
}
