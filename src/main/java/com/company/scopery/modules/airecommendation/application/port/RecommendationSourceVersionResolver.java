package com.company.scopery.modules.airecommendation.application.port;

import java.util.UUID;

public interface RecommendationSourceVersionResolver {
    String resolve(String entityType, UUID entityId);
}
