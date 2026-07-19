package com.company.scopery.modules.raid.decision.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataDecisionOptionJpaRepository extends JpaRepository<DecisionOptionJpaEntity, UUID> {
    List<DecisionOptionJpaEntity> findByDecisionIdOrderByCreatedAtAsc(UUID decisionId);
}
