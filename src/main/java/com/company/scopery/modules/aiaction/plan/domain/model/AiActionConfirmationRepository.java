package com.company.scopery.modules.aiaction.plan.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface AiActionConfirmationRepository {

    AiActionConfirmation save(AiActionConfirmation confirmation);

    Optional<AiActionConfirmation> findById(UUID id);

    Optional<AiActionConfirmation> findLatestByPlanId(UUID planId);
}
