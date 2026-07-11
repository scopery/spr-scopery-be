package com.company.scopery.modules.project.projectphase.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataProjectPhaseJpaRepository
        extends JpaRepository<ProjectPhaseJpaEntity, UUID>, JpaSpecificationExecutor<ProjectPhaseJpaEntity> {

    boolean existsByProjectIdAndCode(UUID projectId, String code);

    boolean existsByProjectIdAndDisplayOrder(UUID projectId, int displayOrder);

    List<ProjectPhaseJpaEntity> findAllByProjectId(UUID projectId);

    @Query("SELECT COUNT(w) > 0 FROM com.company.scopery.modules.project.wbs.infrastructure.persistence.WbsNodeJpaEntity w WHERE w.projectPhaseId = :id AND w.status != 'ARCHIVED'")
    boolean hasActiveWbsNodes(@Param("id") UUID id);

    @Query("SELECT COUNT(t) > 0 FROM com.company.scopery.modules.project.task.infrastructure.persistence.TaskJpaEntity t WHERE t.projectPhaseId = :id AND t.status NOT IN ('ARCHIVED', 'CANCELLED')")
    boolean hasActiveTasks(@Param("id") UUID id);
}
