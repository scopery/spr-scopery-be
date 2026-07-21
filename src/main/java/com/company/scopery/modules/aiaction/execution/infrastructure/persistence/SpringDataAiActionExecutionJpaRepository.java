package com.company.scopery.modules.aiaction.execution.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiActionExecutionJpaRepository extends JpaRepository<AiActionExecutionJpaEntity, UUID> {

    Optional<AiActionExecutionJpaEntity> findByPlanId(UUID planId);

    Optional<AiActionExecutionJpaEntity> findByExecutionKey(String executionKey);

    @Query("SELECT COUNT(e) FROM AiActionExecutionJpaEntity e WHERE e.initiatedByUserId = :userId AND e.status IN :activeStatuses")
    int countByInitiatedByUserIdAndStatusIn(@Param("userId") UUID userId,
                                             @Param("activeStatuses") List<String> activeStatuses);

    @Query(value = """
            SELECT * FROM ai_action_execution e
            WHERE e.status = 'QUEUED'
               OR (e.status = 'RUNNING' AND e.lease_expires_at < NOW())
            ORDER BY e.created_at ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<AiActionExecutionJpaEntity> findClaimable(@Param("limit") int limit);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM AiActionExecutionJpaEntity e WHERE e.id = :id")
    Optional<AiActionExecutionJpaEntity> findAndLockById(@Param("id") UUID id);
}
