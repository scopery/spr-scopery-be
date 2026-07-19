package com.company.scopery.modules.projectfinance.scenario.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataProjectFinanceScenarioJpaRepository
        extends JpaRepository<ProjectFinanceScenarioJpaEntity, UUID> {
    List<ProjectFinanceScenarioJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    Optional<ProjectFinanceScenarioJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    Optional<ProjectFinanceScenarioJpaEntity> findByProjectIdAndCurrentFlagTrue(UUID projectId);
    List<ProjectFinanceScenarioJpaEntity> findByProjectIdAndCurrentFlagTrueOrderByCreatedAtDesc(UUID projectId);
}
