package com.company.scopery.modules.project.task.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface SpringDataTaskJpaRepository
        extends JpaRepository<TaskJpaEntity, UUID>, JpaSpecificationExecutor<TaskJpaEntity> {

    boolean existsByProjectIdAndCode(UUID projectId, String code);

    List<TaskJpaEntity> findAllByWbsNodeId(UUID wbsNodeId);
}
