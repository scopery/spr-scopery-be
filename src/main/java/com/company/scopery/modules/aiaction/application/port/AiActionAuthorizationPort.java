package com.company.scopery.modules.aiaction.application.port;

import java.util.UUID;

public interface AiActionAuthorizationPort {

    void requirePermission(UUID actorId, String permission);

    boolean hasPermission(UUID actorId, String permission);

    void requireTargetAccess(UUID actorId, String targetEntityType, UUID targetEntityId);

    boolean hasTargetAccess(UUID actorId, String targetEntityType, UUID targetEntityId);
}
