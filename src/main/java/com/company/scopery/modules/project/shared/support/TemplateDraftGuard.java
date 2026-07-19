package com.company.scopery.modules.project.shared.support;

import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templateversion.domain.enums.ProjectTemplateVersionStatus;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersion;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersionRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Ensures template structure mutations only happen against a DRAFT version that
 * belongs to the given template path.
 */
@Component
public class TemplateDraftGuard {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateVersionRepository versionRepository;

    public TemplateDraftGuard(ProjectTemplateRepository templateRepository,
                              ProjectTemplateVersionRepository versionRepository) {
        this.templateRepository = templateRepository;
        this.versionRepository = versionRepository;
    }

    public record DraftContext(ProjectTemplate template, ProjectTemplateVersion version) {}

    public DraftContext requireDraft(UUID templateId, UUID versionId) {
        ProjectTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(templateId));
        ProjectTemplateVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> ProjectExceptions.projectTemplateVersionNotFound(versionId));
        if (!version.projectTemplateId().equals(templateId)) {
            throw ProjectExceptions.projectTemplateVersionNotFound(versionId);
        }
        if (version.status() != ProjectTemplateVersionStatus.DRAFT) {
            throw ProjectExceptions.projectTemplateVersionNotDraft(versionId);
        }
        return new DraftContext(template, version);
    }

    public ProjectTemplate requireTemplate(UUID templateId) {
        return templateRepository.findById(templateId)
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(templateId));
    }

    public ProjectTemplateVersion requireVersion(UUID templateId, UUID versionId) {
        ProjectTemplateVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> ProjectExceptions.projectTemplateVersionNotFound(versionId));
        if (!version.projectTemplateId().equals(templateId)) {
            throw ProjectExceptions.projectTemplateVersionNotFound(versionId);
        }
        return version;
    }
}
