package com.company.scopery.modules.project.projectphase.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.projectphase.application.query.SearchProjectPhaseQuery;
import com.company.scopery.modules.project.projectphase.application.response.ProjectPhaseResponse;
import com.company.scopery.modules.project.projectphase.domain.enums.ProjectPhaseStatus;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectSortFields;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProjectPhaseQueryService {

    private final ProjectPhaseRepository projectPhaseRepository;
    private final ProjectWorkspaceAuthorizationService authorizationService;

    public ProjectPhaseQueryService(ProjectPhaseRepository projectPhaseRepository,
                                    ProjectWorkspaceAuthorizationService authorizationService) {
        this.projectPhaseRepository = projectPhaseRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public ProjectPhaseResponse getProjectPhase(UUID projectId, UUID id) {
        var phase = projectPhaseRepository.findById(id)
                .orElseThrow(() -> ProjectExceptions.projectPhaseNotFound(id));

        if (!phase.projectId().equals(projectId)) {
            throw ProjectExceptions.projectPhaseProjectMismatch(id, projectId);
        }

        authorizationService.requireProjectPermission(phase.projectId(), IamAuthorities.PROJECT_PHASE_VIEW);
        return ProjectPhaseResponse.from(phase);
    }

    @Transactional(readOnly = true)
    public PageResult<ProjectPhaseResponse> searchProjectPhases(SearchProjectPhaseQuery query) {
        authorizationService.requireProjectPermission(query.projectId(), IamAuthorities.PROJECT_PHASE_VIEW);

        ProjectPhaseStatus status = ProjectEnumParser.parseOptional(
                ProjectPhaseStatus.class, query.status(),
                "PROJECT_PHASE_STATUS", "status");

        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), ProjectSortFields.DISPLAY_ORDER, true);

        return projectPhaseRepository.search(query.projectId(), status, pageQuery)
                .map(ProjectPhaseResponse::from);
    }
}
