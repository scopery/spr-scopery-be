package com.company.scopery.modules.aiaction.infrastructure.stub;

import com.company.scopery.modules.aiaction.application.port.AiActionAuditReferencePort;
import org.springframework.stereotype.Component;

import java.util.UUID;

// Stub implementation — replaced by real audit write
@Component
public class StubAiActionAuditReferencePort implements AiActionAuditReferencePort {

    @Override
    public String writeAuditRecord(UUID executionId, UUID stepId, String toolCode,
                                   UUID actorId, String outcome, String details) {
        return "stub-audit-ref-" + UUID.randomUUID();
    }
}
