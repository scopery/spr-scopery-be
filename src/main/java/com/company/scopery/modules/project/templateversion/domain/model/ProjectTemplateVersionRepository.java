package com.company.scopery.modules.project.templateversion.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectTemplateVersionRepository {

    ProjectTemplateVersion save(ProjectTemplateVersion version);

    Optional<ProjectTemplateVersion> findById(UUID id);

    List<ProjectTemplateVersion> findByTemplateId(UUID templateId);

    Optional<Integer> findMaxVersionNumber(UUID templateId);
}
