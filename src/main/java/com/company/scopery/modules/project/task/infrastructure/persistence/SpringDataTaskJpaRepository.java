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

    @Query(value = """
            SELECT t.id, t.code, t.title, t.project_id,
                   p.name  AS project_name,
                   t.project_phase_id, ph.name AS phase_name,
                   t.in_charge_user_id, t.completed_at, t.started_at,
                   t.estimate_hours,
                   CASE
                       WHEN t.completed_at IS NOT NULL
                            AND (t.completed_at AT TIME ZONE 'UTC')::date = :date
                       THEN 'DONE'
                       ELSE 'IN_PROGRESS'
                   END AS day_status
            FROM project_task t
            JOIN project_project p ON p.id = t.project_id
            LEFT JOIN project_project_phase ph ON ph.id = t.project_phase_id
            WHERE p.workspace_id = :workspaceId
              AND t.in_charge_user_id IN :userIds
              AND t.status NOT IN ('CANCELLED', 'ARCHIVED')
              AND (
                  (t.completed_at IS NOT NULL
                   AND (t.completed_at AT TIME ZONE 'UTC')::date = :date)
                  OR (t.started_at IS NOT NULL
                      AND (t.started_at AT TIME ZONE 'UTC')::date <= :date
                      AND (t.completed_at IS NULL
                           OR (t.completed_at AT TIME ZONE 'UTC')::date > :date))
                  OR (t.status = 'IN_PROGRESS'
                      AND t.started_at IS NULL
                      AND :date <= CURRENT_DATE)
              )
            """, nativeQuery = true)
    List<Object[]> findDailySummaryRows(@Param("workspaceId") UUID workspaceId,
                                        @Param("userIds") Collection<UUID> userIds,
                                        @Param("date") LocalDate date);
}
