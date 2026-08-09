package com.company.scopery.integration.ai.anthropic;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scopery.aiagent.provider.anthropic")
public record AnthropicProperties(
        String baseUrl,
        int timeoutSeconds,
        String apiKey
) {}
