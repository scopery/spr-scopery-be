package com.company.scopery.modules.aiagent.agent.application.query;

public record SearchAgentQuery(
        String keyword,
        String type,
        String status,
        String outputFormat,
        int page,
        int size
) {}