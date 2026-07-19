package com.company.scopery.modules.airecommendation.infrastructure.port;

import com.company.scopery.modules.airecommendation.application.port.RecommendationSourceVersionResolver;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NoOpRecommendationSourceVersionResolverAdapter implements RecommendationSourceVersionResolver {

    @Override
    public String resolve(String entityType, UUID entityId) {
        return null;
    }
}
