package com.company.scopery.integration.ai;

import java.util.Map;

/**
 * Represents a tool definition sent to an LLM provider (OpenAI function-calling format).
 * name and description must be in English.
 */
public record AiLlmToolDefinition(
        String name,
        String description,
        Map<String, Object> parameters
) {}
