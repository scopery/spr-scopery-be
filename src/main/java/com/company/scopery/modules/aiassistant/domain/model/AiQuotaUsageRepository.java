package com.company.scopery.modules.aiassistant.domain.model;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface AiQuotaUsageRepository {

    AiQuotaUsage save(AiQuotaUsage usage);

    Optional<AiQuotaUsage> findByWorkspaceIdAndActorIdAndUsageDate(UUID workspaceId, UUID actorId, LocalDate date);

    Optional<AiQuotaUsage> findByWorkspaceIdAndActorIdAndUsageDateForUpdate(UUID workspaceId, UUID actorId, LocalDate date);
}
