package com.company.scopery.modules.aiaction.infrastructure.stub;

import com.company.scopery.modules.aiaction.application.port.AiActionAuthorizationPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

// Stub implementation — replaced by real IAM wiring
@Component
public class StubAiActionAuthorizationPort implements AiActionAuthorizationPort {

    @Override
    public void requirePermission(UUID actorId, String permission) {
        // no-op: allow all until IAM is wired
    }

    @Override
    public boolean hasPermission(UUID actorId, String permission) {
        return true;
    }

    @Override
    public void requireTargetAccess(UUID actorId, String targetEntityType, UUID targetEntityId) {
        // no-op
    }

    @Override
    public boolean hasTargetAccess(UUID actorId, String targetEntityType, UUID targetEntityId) {
        return true;
    }
}
