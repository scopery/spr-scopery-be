package com.company.scopery.modules.aiagent.provider.application.command;

public record CreateProviderCommand(
        String name,
        String code,
        String type,
        String apiBaseUrl,
        String description
) {}