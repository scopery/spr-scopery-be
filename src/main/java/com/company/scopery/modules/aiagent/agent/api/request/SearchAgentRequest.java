package com.company.scopery.modules.aiagent.agent.api.request;

public record SearchAgentRequest(
        String keyword,
        String type,
        String status,
        String outputFormat,
        int page,
        int size
) {}