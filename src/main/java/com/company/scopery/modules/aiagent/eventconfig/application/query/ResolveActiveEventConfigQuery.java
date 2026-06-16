package com.company.scopery.modules.aiagent.eventconfig.application.query;

import java.util.UUID;

public record ResolveActiveEventConfigQuery(
        UUID eventDefinitionId,
        String sourceSystem,
        String eventKey,
        String environment
) {}