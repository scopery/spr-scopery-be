package com.company.scopery.modules.finance.scenario.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataFinanceScenarioJpaRepository extends JpaRepository<FinanceScenarioJpaEntity, UUID> {

    Optional<FinanceScenarioJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);

    Optional<FinanceScenarioJpaEntity> findByProjectIdAndCurrentFlagTrue(UUID projectId);

    boolean existsByProjectIdAndCode(UUID projectId, String code);

    List<FinanceScenarioJpaEntity> findAllByProjectIdOrderByCreatedAtDesc(UUID projectId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE FinanceScenarioJpaEntity e SET e.currentFlag = false, e.updatedAt = CURRENT_TIMESTAMP WHERE e.projectId = :projectId AND e.currentFlag = true")
    int clearCurrentFlag(@Param("projectId") UUID projectId);
}
