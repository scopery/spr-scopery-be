package com.company.scopery.modules.aiaction.plan.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiActionStepRepository {

    AiActionStep save(AiActionStep step);

    List<AiActionStep> saveAll(List<AiActionStep> steps);

    List<AiActionStep> findByPlanIdOrderByOrdinal(UUID planId);

    Optional<AiActionStep> findById(UUID id);
}
