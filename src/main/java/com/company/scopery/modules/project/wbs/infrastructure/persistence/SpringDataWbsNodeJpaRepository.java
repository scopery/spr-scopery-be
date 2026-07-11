package com.company.scopery.modules.project.wbs.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataWbsNodeJpaRepository
        extends JpaRepository<WbsNodeJpaEntity, UUID>, JpaSpecificationExecutor<WbsNodeJpaEntity> {

    boolean existsByProjectIdAndCode(UUID projectId, String code);

    boolean existsByParentIdAndSortOrder(UUID parentId, int sortOrder);

    boolean existsByProjectIdAndParentIdIsNullAndSortOrder(UUID projectId, int sortOrder);

    @Query("SELECT n FROM WbsNodeJpaEntity n WHERE n.path LIKE CONCAT(:nodePath, '/%') AND n.status != 'ARCHIVED'")
    List<WbsNodeJpaEntity> findAllDescendants(@Param("nodePath") String nodePath);

    @Query("SELECT COUNT(t) > 0 FROM com.company.scopery.modules.project.task.infrastructure.persistence.TaskJpaEntity t WHERE t.wbsNodeId = :id AND t.status NOT IN ('ARCHIVED', 'CANCELLED')")
    boolean hasLinkedActiveTasks(@Param("id") UUID id);

    @Query("SELECT COUNT(c) > 0 FROM WbsNodeJpaEntity c WHERE c.parentId = :id AND c.status != 'ARCHIVED'")
    boolean hasActiveChildren(@Param("id") UUID id);

    List<WbsNodeJpaEntity> findAllByProjectId(UUID projectId);
}
