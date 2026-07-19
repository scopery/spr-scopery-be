package com.company.scopery.modules.project.milestone.application.service;

import com.company.scopery.modules.project.milestone.application.response.MilestoneResponse;
import com.company.scopery.modules.project.milestone.domain.model.ProjectMilestoneRepository;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectMilestoneQueryService {

    private final ProjectMilestoneRepository milestones;
    private final ProjectWorkspaceAuthorizationService authorization;

    public ProjectMilestoneQueryService(ProjectMilestoneRepository milestones,
                                        ProjectWorkspaceAuthorizationService authorization) {
        this.milestones = milestones;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<MilestoneResponse> list(UUID projectId) {
        authorization.requireGanttView(projectId);
        return milestones.findAllByProjectId(projectId).stream().map(MilestoneResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public MilestoneResponse get(UUID projectId, UUID milestoneId) {
        authorization.requireGanttView(projectId);
        var m = milestones.findById(milestoneId)
                .orElseThrow(() -> ProjectExceptions.projectMilestoneNotFound(milestoneId));
        if (!m.projectId().equals(projectId)) {
            throw ProjectExceptions.projectMilestonePathMismatch(milestoneId, projectId);
        }
        return MilestoneResponse.from(m);
    }
}
