package com.company.scopery.modules.aiagent.eventconfig.application.query;

import java.util.UUID;

public record SearchEventConfigQuery(
        String keyword,
        UUID eventDefinitionId,
        String environment,
        String triggerType,
        String status,
        UUID agentId,
        int page,
        int size
) {}