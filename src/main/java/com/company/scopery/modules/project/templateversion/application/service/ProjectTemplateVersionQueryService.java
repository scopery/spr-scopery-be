package com.company.scopery.modules.project.templateversion.application.service;

import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templateversion.application.response.ProjectTemplateVersionResponse;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersion;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectTemplateVersionQueryService {
    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateVersionRepository versionRepository;
    private final TemplateAccessSupport accessSupport;

    public ProjectTemplateVersionQueryService(ProjectTemplateRepository templateRepository,
                                              ProjectTemplateVersionRepository versionRepository,
                                              TemplateAccessSupport accessSupport) {
        this.templateRepository = templateRepository;
        this.versionRepository = versionRepository;
        this.accessSupport = accessSupport;
    }

    @Transactional(readOnly = true)
    public List<ProjectTemplateVersionResponse> list(UUID templateId) {
        ProjectTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(templateId));
        accessSupport.requireView(template);
        return versionRepository.findByTemplateId(templateId).stream().map(ProjectTemplateVersionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProjectTemplateVersionResponse get(UUID templateId, UUID versionId) {
        ProjectTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(templateId));
        accessSupport.requireView(template);
        ProjectTemplateVersion version = versionRepository.findById(versionId)
                .filter(v -> v.projectTemplateId().equals(templateId))
                .orElseThrow(() -> ProjectExceptions.projectTemplateVersionNotFound(versionId));
        return ProjectTemplateVersionResponse.from(version);
    }
}
