package com.company.scopery.modules.aiaction.application.port;

import java.util.Optional;
import java.util.UUID;

public interface AiActionTargetVersionResolver {

    Optional<String> resolveVersionToken(String entityType, UUID entityId);

    String requireVersionToken(String entityType, UUID entityId);
}
