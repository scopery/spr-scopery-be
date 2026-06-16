package com.company.scopery.integration.ai.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenAiResponsesResponse(
        String id,
        List<OutputItem> output,
        Usage usage
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OutputItem(
            String type,
            List<ContentItem> content
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ContentItem(
            String type,
            String text
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Usage(
            @JsonProperty("input_tokens") Integer inputTokens,
            @JsonProperty("output_tokens") Integer outputTokens,
            @JsonProperty("total_tokens") Integer totalTokens
    ) {}

    public String extractOutputText() {
        if (output == null || output.isEmpty()) return null;
        return output.stream()
                .filter(item -> "message".equals(item.type()))
                .flatMap(item -> item.content() != null ? item.content().stream() : Stream.empty())
                .filter(c -> "output_text".equals(c.type()))
                .map(ContentItem::text)
                .findFirst()
                .orElse(null);
    }
}