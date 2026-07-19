package com.company.scopery.modules.project.template.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectSortFields;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import com.company.scopery.modules.project.template.application.query.SearchProjectTemplateQuery;
import com.company.scopery.modules.project.template.application.response.ProjectTemplateResponse;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateCategory;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateScope;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateStatus;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProjectTemplateQueryService {

    private final ProjectTemplateRepository templateRepository;
    private final TemplateAccessSupport accessSupport;
    private final ProjectWorkspaceAuthorizationService workspaceAuthorizationService;

    public ProjectTemplateQueryService(ProjectTemplateRepository templateRepository,
                                       TemplateAccessSupport accessSupport,
                                       ProjectWorkspaceAuthorizationService workspaceAuthorizationService) {
        this.templateRepository = templateRepository;
        this.accessSupport = accessSupport;
        this.workspaceAuthorizationService = workspaceAuthorizationService;
    }

    @Transactional(readOnly = true)
    public ProjectTemplateResponse getTemplate(UUID id) {
        ProjectTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(id));
        accessSupport.requireView(template);
        return ProjectTemplateResponse.from(template);
    }

    @Transactional(readOnly = true)
    public PageResult<ProjectTemplateResponse> search(SearchProjectTemplateQuery query) {
        if (query.workspaceId() != null) {
            workspaceAuthorizationService.requireTemplateView(query.workspaceId());
        }

        ProjectTemplateScope scope = ProjectEnumParser.parseOptional(
                ProjectTemplateScope.class, query.scope(), "PROJECT_TEMPLATE_INVALID_SCOPE", "scope");
        ProjectTemplateStatus status = ProjectEnumParser.parseOptional(
                ProjectTemplateStatus.class, query.status(), "PROJECT_TEMPLATE_INVALID_STATUS", "status");
        ProjectTemplateCategory category = ProjectEnumParser.parseOptional(
                ProjectTemplateCategory.class, query.category(), "PROJECT_TEMPLATE_INVALID_CATEGORY", "category");

        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), ProjectSortFields.CREATED_AT, false);
        return templateRepository.search(
                        scope, query.workspaceId(), query.organizationId(), status, category, query.keyword(), pageQuery)
                .map(ProjectTemplateResponse::from);
    }
}
