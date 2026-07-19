package com.company.scopery.modules.aiagent.tool.application.query;

public record SearchAiToolQuery(
        String category,
        String status,
        String codeOrName,
        int page,
        int size
) {}
