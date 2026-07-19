package com.company.scopery.modules.raid.decision.domain.model;
import java.util.Optional; import java.util.UUID;
public interface DecisionImpactRepository {
    DecisionImpact save(DecisionImpact impact);
    Optional<DecisionImpact> findByDecisionId(UUID decisionId);
}
