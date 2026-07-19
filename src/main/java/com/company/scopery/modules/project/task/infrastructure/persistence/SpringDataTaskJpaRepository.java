package com.company.scopery.modules.project.task.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface SpringDataTaskJpaRepository
        extends JpaRepository<TaskJpaEntity, UUID>, JpaSpecificationExecutor<TaskJpaEntity> {

    boolean existsByProjectIdAndCode(UUID projectId, String code);

    List<TaskJpaEntity> findAllByWbsNodeId(UUID wbsNodeId);

    List<TaskJpaEntity> findAllByProjectId(UUID projectId);

    @Query("SELECT t FROM TaskJpaEntity t WHERE t.dueDate = :dueDate AND t.status NOT IN :excluded")
    List<TaskJpaEntity> findDueSoonCandidates(@Param("dueDate") LocalDate dueDate,
                                              @Param("excluded") Collection<String> excluded,
                                              Pageable pageable);

    @Query("SELECT t FROM TaskJpaEntity t WHERE t.dueDate < :beforeDate AND t.status NOT IN :excluded")
    List<TaskJpaEntity> findOverdueCandidates(@Param("beforeDate") LocalDate beforeDate,
                                              @Param("excluded") Collection<String> excluded,
                                              Pageable pageable);
}
