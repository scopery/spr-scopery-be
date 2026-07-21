package com.company.scopery.modules.aiaction.application.port;

import java.util.UUID;

public interface AiActionAuditReferencePort {

    String writeAuditRecord(UUID executionId, UUID stepId, String toolCode,
                            UUID actorId, String outcome, String details);
}
