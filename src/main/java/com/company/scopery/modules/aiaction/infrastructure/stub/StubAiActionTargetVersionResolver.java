package com.company.scopery.modules.aiaction.infrastructure.stub;

import com.company.scopery.modules.aiaction.application.port.AiActionTargetVersionResolver;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

// Stub implementation — replaced by domain-specific resolvers
@Component
public class StubAiActionTargetVersionResolver implements AiActionTargetVersionResolver {

    @Override
    public Optional<String> resolveVersionToken(String entityType, UUID entityId) {
        return Optional.of("stub-version-token");
    }

    @Override
    public String requireVersionToken(String entityType, UUID entityId) {
        return "stub-version-token";
    }
}
