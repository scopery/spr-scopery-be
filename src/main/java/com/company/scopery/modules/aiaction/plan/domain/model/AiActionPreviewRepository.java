package com.company.scopery.modules.aiaction.plan.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface AiActionPreviewRepository {

    AiActionPreview save(AiActionPreview preview);

    Optional<AiActionPreview> findByPlanId(UUID planId);
}
