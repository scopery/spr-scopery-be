package com.company.scopery.modules.aiaction.application.port;

import java.util.UUID;

public interface AiActionBaselineGuardPort {

    boolean isBaselineLocked(String entityType, UUID entityId);

    void requireNotLocked(String entityType, UUID entityId);
}
