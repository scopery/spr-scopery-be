package com.company.scopery.modules.raid.decision.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataDecisionImpactJpaRepository extends JpaRepository<DecisionImpactJpaEntity, UUID> {
    Optional<DecisionImpactJpaEntity> findByDecisionId(UUID decisionId);
}
