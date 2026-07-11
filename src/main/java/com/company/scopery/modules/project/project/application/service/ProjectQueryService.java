package com.company.scopery.modules.project.project.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.application.query.SearchProjectQuery;
import com.company.scopery.modules.project.project.application.response.ProjectResponse;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectSortFields;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProjectQueryService {

    private final ProjectRepository projectRepository;
    private final ProjectWorkspaceAuthorizationService authorizationService;

    public ProjectQueryService(ProjectRepository projectRepository,
                               ProjectWorkspaceAuthorizationService authorizationService) {
        this.projectRepository = projectRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(id));
        authorizationService.requireWorkspacePermission(project.workspaceId(), IamAuthorities.PROJECT_VIEW);
        return ProjectResponse.from(project);
    }

    @Transactional(readOnly = true)
    public PageResult<ProjectResponse> searchProjects(SearchProjectQuery query) {
        authorizationService.requireWorkspacePermission(query.workspaceId(), IamAuthorities.PROJECT_VIEW);

        ProjectStatus status = ProjectEnumParser.parseOptional(
                ProjectStatus.class, query.status(),
                "PROJECT_INVALID_STATUS", "status");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), ProjectSortFields.CREATED_AT, false);
        return projectRepository.search(query.workspaceId(), query.keyword(), status, pageQuery)
                .map(ProjectResponse::from);
    }
}
