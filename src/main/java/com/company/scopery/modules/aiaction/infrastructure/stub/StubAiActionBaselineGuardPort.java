package com.company.scopery.modules.aiaction.infrastructure.stub;

import com.company.scopery.modules.aiaction.application.port.AiActionBaselineGuardPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

// Stub implementation — replaced by real baseline guard
@Component
public class StubAiActionBaselineGuardPort implements AiActionBaselineGuardPort {

    @Override
    public boolean isBaselineLocked(String entityType, UUID entityId) {
        return false;
    }

    @Override
    public void requireNotLocked(String entityType, UUID entityId) {
        // no-op
    }
}
