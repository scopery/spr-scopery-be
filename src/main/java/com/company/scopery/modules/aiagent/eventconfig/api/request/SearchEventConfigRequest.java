package com.company.scopery.modules.aiagent.eventconfig.api.request;

import java.util.UUID;

public record SearchEventConfigRequest(
        String keyword,
        UUID eventDefinitionId,
        String environment,
        String triggerType,
        String status,
        UUID agentId,
        int page,
        int size
) {}
