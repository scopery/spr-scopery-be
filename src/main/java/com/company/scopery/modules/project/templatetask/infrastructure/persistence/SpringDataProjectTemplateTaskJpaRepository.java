package com.company.scopery.modules.project.templatetask.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataProjectTemplateTaskJpaRepository
        extends JpaRepository<ProjectTemplateTaskJpaEntity, UUID> {

    List<ProjectTemplateTaskJpaEntity> findByTemplateVersionIdOrderByTitleAsc(UUID templateVersionId);

    boolean existsByTemplatePhaseId(UUID templatePhaseId);

    boolean existsByTemplateWbsNodeId(UUID templateWbsNodeId);
}
