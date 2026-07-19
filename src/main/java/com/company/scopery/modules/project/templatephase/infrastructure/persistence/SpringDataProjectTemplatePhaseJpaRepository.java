package com.company.scopery.modules.project.templatephase.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataProjectTemplatePhaseJpaRepository
        extends JpaRepository<ProjectTemplatePhaseJpaEntity, UUID> {

    List<ProjectTemplatePhaseJpaEntity> findByTemplateVersionIdOrderByDisplayOrderAsc(UUID templateVersionId);

    boolean existsByPhaseDefinitionId(UUID phaseDefinitionId);

    boolean existsByTemplateVersionIdAndDisplayOrder(UUID templateVersionId, int displayOrder);
}
