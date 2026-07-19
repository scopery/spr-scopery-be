package com.company.scopery.modules.raid.decisionlink.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataDecisionLinkJpaRepository extends JpaRepository<DecisionLinkJpaEntity, UUID> {
    List<DecisionLinkJpaEntity> findByDecisionIdOrderByCreatedAtAsc(UUID decisionId);
    Optional<DecisionLinkJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    void deleteByIdAndProjectId(UUID id, UUID projectId);
}
