package com.company.scopery.modules.project.templatedependency.application.service;

import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templatedependency.application.response.ProjectTemplateTaskDependencyResponse;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependencyRepository;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersion;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectTemplateTaskDependencyQueryService {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateVersionRepository versionRepository;
    private final ProjectTemplateTaskDependencyRepository dependencyRepository;
    private final TemplateAccessSupport authorizationSupport;

    public ProjectTemplateTaskDependencyQueryService(
            ProjectTemplateRepository templateRepository,
            ProjectTemplateVersionRepository versionRepository,
            ProjectTemplateTaskDependencyRepository dependencyRepository,
            TemplateAccessSupport authorizationSupport) {
        this.templateRepository = templateRepository;
        this.versionRepository = versionRepository;
        this.dependencyRepository = dependencyRepository;
        this.authorizationSupport = authorizationSupport;
    }

    @Transactional(readOnly = true)
    public List<ProjectTemplateTaskDependencyResponse> listDependencies(UUID templateId, UUID versionId) {
        ProjectTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(templateId));
        authorizationSupport.requireView(template);
        ProjectTemplateVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> ProjectExceptions.projectTemplateVersionNotFound(versionId));
        if (!version.projectTemplateId().equals(templateId)) {
            throw ProjectExceptions.projectTemplateVersionNotFound(versionId);
        }
        return dependencyRepository.findByTemplateVersionId(versionId).stream()
                .map(ProjectTemplateTaskDependencyResponse::from)
                .toList();
    }
}
