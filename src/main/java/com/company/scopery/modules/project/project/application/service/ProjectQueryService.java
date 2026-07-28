package com.company.scopery.modules.project.project.application.service;

import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.application.query.SearchProjectQuery;
import com.company.scopery.modules.project.project.application.response.ProjectResponse;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class ProjectQueryService {

    private final ProjectRepository projectRepository;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;

    public ProjectQueryService(
            ProjectRepository projectRepository,
            ProjectWorkspaceAuthorizationService authorizationService,
            CurrentUserAuthorizationService currentUserService,
            WorkspaceIamIntegrationService iamIntegrationService) {
        this.projectRepository = projectRepository;
        this.authorizationService = authorizationService;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(id));
        authorizationService.requireProjectViewByProjectId(id);
        return ProjectResponse.from(project);
    }

    @Transactional(readOnly = true)
    public PageResult<ProjectResponse> searchProjects(SearchProjectQuery query) {
        // Gate: may open workspace Projects section
        authorizationService.requireProjectView(query.workspaceId());

        ProjectStatus status = ProjectEnumParser.parseOptional(
                ProjectStatus.class, query.status(),
                "PROJECT_INVALID_STATUS", "status");

        UUID userId = currentUserService.resolveCurrentUser().id();
        String keyword = query.keyword() == null ? null : query.keyword().trim().toLowerCase(Locale.ROOT);

        List<Project> visible = projectRepository.findAllByWorkspaceId(query.workspaceId()).stream()
                .filter(p -> status == null || p.status() == status)
                .filter(p -> matchesKeyword(p, keyword))
                .filter(p -> iamIntegrationService.canViewProject(
                        p.id(), query.workspaceId(), userId, IamAuthorities.PROJECT_VIEW))
                .sorted(Comparator.comparing(Project::createdAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        int page = Math.max(query.page(), 0);
        int size = Math.max(query.size(), 1);
        int from = Math.min(page * size, visible.size());
        int to = Math.min(from + size, visible.size());
        List<ProjectResponse> content = visible.subList(from, to).stream().map(ProjectResponse::from).toList();
        long total = visible.size();
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) total / size);

        return new PageResult<>(
                content,
                page,
                size,
                total,
                totalPages,
                page == 0,
                page >= Math.max(totalPages - 1, 0));
    }

    private static boolean matchesKeyword(Project project, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        String name = project.name() == null ? "" : project.name().toLowerCase(Locale.ROOT);
        String code = project.code() == null ? "" : project.code().toLowerCase(Locale.ROOT);
        return name.contains(keyword) || code.contains(keyword);
    }
}
