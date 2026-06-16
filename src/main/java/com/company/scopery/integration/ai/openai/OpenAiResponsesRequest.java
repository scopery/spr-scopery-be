package com.company.scopery.integration.ai.openai;

public record OpenAiResponsesRequest(
        String model,
        String input
) {}