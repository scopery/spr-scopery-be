package com.company.scopery.modules.raid.decision.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface DecisionOptionRepository {
    DecisionOption save(DecisionOption o);
    Optional<DecisionOption> findById(UUID id);
    List<DecisionOption> findByDecisionId(UUID decisionId);
    void deleteById(UUID id);
}
