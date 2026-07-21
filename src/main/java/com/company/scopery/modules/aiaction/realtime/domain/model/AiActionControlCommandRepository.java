package com.company.scopery.modules.aiaction.realtime.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiActionControlCommandRepository {

    AiActionControlCommand save(AiActionControlCommand command);

    Optional<AiActionControlCommand> findByIdempotencyKey(String idempotencyKey);

    List<AiActionControlCommand> findPendingByExecutionId(UUID executionId);
}
