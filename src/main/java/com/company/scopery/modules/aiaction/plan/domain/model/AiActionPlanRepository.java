package com.company.scopery.modules.aiaction.plan.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiActionPlanRepository {

    AiActionPlan save(AiActionPlan plan);

    Optional<AiActionPlan> findById(UUID id);

    Optional<AiActionPlan> findLatestByRequestId(UUID requestId);
}
