package com.company.scopery.integration.ai.anthropic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AnthropicRequest(
        String model,
        Object system,
        List<Map<String, String>> messages,
        @JsonProperty("max_tokens") int maxTokens,
        BigDecimal temperature,
        boolean stream
) {}
