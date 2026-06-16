package com.company.scopery.integration.ai.openai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scopery.aiagent.provider.openai")
public record OpenAiProperties(
        String baseUrl,
        int timeoutSeconds
) {}