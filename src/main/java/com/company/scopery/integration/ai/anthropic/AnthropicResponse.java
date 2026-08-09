package com.company.scopery.integration.ai.anthropic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AnthropicResponse(
        String id,
        List<ContentBlock> content,
        Usage usage
) {
    public record ContentBlock(String type, String text) {}

    public record Usage(
            @JsonProperty("input_tokens") Integer inputTokens,
            @JsonProperty("output_tokens") Integer outputTokens
    ) {}

    public String extractOutputText() {
        if (content == null || content.isEmpty()) return "";
        return content.stream()
                .filter(b -> "text".equals(b.type()) && b.text() != null)
                .map(ContentBlock::text)
                .reduce("", String::concat);
    }
}
