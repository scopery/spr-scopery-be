package com.company.scopery.modules.project.shared.support;

import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.templateversion.domain.enums.ProjectTemplateVersionStatus;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersion;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersionRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Ensures structural mutations (phase / WBS / task / dependency) only run against a DRAFT
 * template version that belongs to the path templateId.
 */
@Component
public class TemplateVersionMutationGuard {

    private final ProjectTemplateVersionRepository versionRepository;

    public TemplateVersionMutationGuard(ProjectTemplateVersionRepository versionRepository) {
        this.versionRepository = versionRepository;
    }

    public ProjectTemplateVersion requireDraftVersion(UUID templateId, UUID versionId) {
        ProjectTemplateVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> ProjectExceptions.projectTemplateVersionNotFound(versionId));

        if (!version.projectTemplateId().equals(templateId)) {
            throw ProjectExceptions.projectTemplateVersionNotFound(versionId);
        }

        if (version.status() != ProjectTemplateVersionStatus.DRAFT) {
            throw ProjectExceptions.projectTemplateVersionNotDraft(versionId);
        }

        return version;
    }
}
