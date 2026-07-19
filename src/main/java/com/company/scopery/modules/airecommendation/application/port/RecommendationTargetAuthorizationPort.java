package com.company.scopery.modules.airecommendation.application.port;

import java.util.UUID;

public interface RecommendationTargetAuthorizationPort {
    boolean checkCapability(UUID actorId, String entityType, UUID entityId, String capabilityCode);
}
