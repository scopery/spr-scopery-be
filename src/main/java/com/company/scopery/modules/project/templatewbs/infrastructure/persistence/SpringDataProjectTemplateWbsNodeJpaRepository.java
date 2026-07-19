package com.company.scopery.modules.project.templatewbs.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataProjectTemplateWbsNodeJpaRepository
        extends JpaRepository<ProjectTemplateWbsNodeJpaEntity, UUID> {

    List<ProjectTemplateWbsNodeJpaEntity> findByTemplateVersionIdOrderByDepthAscOrderIndexAsc(UUID templateVersionId);

    List<ProjectTemplateWbsNodeJpaEntity> findByParentIdOrderByOrderIndexAsc(UUID parentId);

    boolean existsByTemplatePhaseId(UUID templatePhaseId);
}
