package com.company.scopery.modules.aiplanning.contextsnapshot.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface AiPlanningContextSnapshotRepository {
    AiPlanningContextSnapshot save(AiPlanningContextSnapshot snapshot);
    Optional<AiPlanningContextSnapshot> findById(UUID id);
}
