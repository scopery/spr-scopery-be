package com.company.scopery.modules.aiagent.execution.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface SpringDataExecutionLogJpaRepository
        extends JpaRepository<ExecutionLogJpaEntity, UUID>,
                JpaSpecificationExecutor<ExecutionLogJpaEntity> {

    boolean existsByRequestId(String requestId);

    // ── GLOBAL aggregation ────────────────────────────────────────────────────

    @Query("SELECT COUNT(e) FROM ExecutionLogJpaEntity e WHERE e.createdAt >= :start AND e.createdAt < :end AND e.status IN ('RUNNING', 'SUCCEEDED')")
    long countRequestsGlobal(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT SUM(e.totalTokenCount) FROM ExecutionLogJpaEntity e WHERE e.createdAt >= :start AND e.createdAt < :end AND e.status = 'SUCCEEDED'")
    Long sumTotalTokensGlobal(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT SUM(e.estimatedCost) FROM ExecutionLogJpaEntity e WHERE e.createdAt >= :start AND e.createdAt < :end AND e.status = 'SUCCEEDED'")
    BigDecimal sumEstimatedCostGlobal(@Param("start") Instant start, @Param("end") Instant end);

    // ── EVENT_CONFIG aggregation ──────────────────────────────────────────────

    @Query("SELECT COUNT(e) FROM ExecutionLogJpaEntity e WHERE e.createdAt >= :start AND e.createdAt < :end AND e.status IN ('RUNNING', 'SUCCEEDED') AND e.eventConfigId = :id")
    long countRequestsByEventConfig(@Param("id") UUID id, @Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT SUM(e.totalTokenCount) FROM ExecutionLogJpaEntity e WHERE e.createdAt >= :start AND e.createdAt < :end AND e.status = 'SUCCEEDED' AND e.eventConfigId = :id")
    Long sumTotalTokensByEventConfig(@Param("id") UUID id, @Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT SUM(e.estimatedCost) FROM ExecutionLogJpaEntity e WHERE e.createdAt >= :start AND e.createdAt < :end AND e.status = 'SUCCEEDED' AND e.eventConfigId = :id")
    BigDecimal sumEstimatedCostByEventConfig(@Param("id") UUID id, @Param("start") Instant start, @Param("end") Instant end);

    // ── AGENT aggregation ─────────────────────────────────────────────────────

    @Query("SELECT COUNT(e) FROM ExecutionLogJpaEntity e WHERE e.createdAt >= :start AND e.createdAt < :end AND e.status IN ('RUNNING', 'SUCCEEDED') AND e.agentId = :id")
    long countRequestsByAgent(@Param("id") UUID id, @Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT SUM(e.totalTokenCount) FROM ExecutionLogJpaEntity e WHERE e.createdAt >= :start AND e.createdAt < :end AND e.status = 'SUCCEEDED' AND e.agentId = :id")
    Long sumTotalTokensByAgent(@Param("id") UUID id, @Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT SUM(e.estimatedCost) FROM ExecutionLogJpaEntity e WHERE e.createdAt >= :start AND e.createdAt < :end AND e.status = 'SUCCEEDED' AND e.agentId = :id")
    BigDecimal sumEstimatedCostByAgent(@Param("id") UUID id, @Param("start") Instant start, @Param("end") Instant end);

    // ── MODEL_DEPLOYMENT aggregation ──────────────────────────────────────────

    @Query("SELECT COUNT(e) FROM ExecutionLogJpaEntity e WHERE e.createdAt >= :start AND e.createdAt < :end AND e.status IN ('RUNNING', 'SUCCEEDED') AND e.modelDeploymentId = :id")
    long countRequestsByModelDeployment(@Param("id") UUID id, @Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT SUM(e.totalTokenCount) FROM ExecutionLogJpaEntity e WHERE e.createdAt >= :start AND e.createdAt < :end AND e.status = 'SUCCEEDED' AND e.modelDeploymentId = :id")
    Long sumTotalTokensByModelDeployment(@Param("id") UUID id, @Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT SUM(e.estimatedCost) FROM ExecutionLogJpaEntity e WHERE e.createdAt >= :start AND e.createdAt < :end AND e.status = 'SUCCEEDED' AND e.modelDeploymentId = :id")
    BigDecimal sumEstimatedCostByModelDeployment(@Param("id") UUID id, @Param("start") Instant start, @Param("end") Instant end);
}
