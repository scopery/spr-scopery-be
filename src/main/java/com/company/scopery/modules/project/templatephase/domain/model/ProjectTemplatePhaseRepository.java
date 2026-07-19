package com.company.scopery.modules.project.templatephase.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectTemplatePhaseRepository {

    ProjectTemplatePhase save(ProjectTemplatePhase phase);

    Optional<ProjectTemplatePhase> findById(UUID id);

    void deleteById(UUID id);

    List<ProjectTemplatePhase> findByTemplateVersionId(UUID templateVersionId);

    boolean existsByPhaseDefinitionId(UUID phaseDefinitionId);

    boolean existsByTemplateVersionIdAndDisplayOrder(UUID templateVersionId, int displayOrder);
}
