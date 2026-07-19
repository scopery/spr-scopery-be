package com.company.scopery.modules.raid.decisionlink.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DecisionLinkRepository {
    DecisionLink save(DecisionLink link);
    Optional<DecisionLink> findByIdAndProjectId(UUID id, UUID projectId);
    List<DecisionLink> findByDecisionId(UUID decisionId);
    void deleteByIdAndProjectId(UUID id, UUID projectId);
}
