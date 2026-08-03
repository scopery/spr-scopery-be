package com.company.scopery.modules.traceability.aimapping.application.internal;

import com.company.scopery.modules.traceability.aimapping.summary.domain.model.MappingSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MappingPromptBuilderService {

    private final ObjectMapper objectMapper;

    public MappingPromptBuilderService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public record ScoredCandidate(MappingSummary summary, double retrievalScore, int rank) {}

    public String buildInputJson(MappingSummary source, List<ScoredCandidate> candidates) {
        Map<String, Object> payload = new LinkedHashMap<>();

        Map<String, Object> sourceMap = new LinkedHashMap<>();
        sourceMap.put("id", source.entityId().toString());
        sourceMap.put("type", source.entityType().name());
        sourceMap.put("summary", parseOrRaw(source.structuredJson()));
        sourceMap.put("compactText", source.compactText());
        payload.put("source", sourceMap);

        List<Map<String, Object>> candidateList = candidates.stream()
                .map(c -> {
                    Map<String, Object> cm = new LinkedHashMap<>();
                    cm.put("id", c.summary().entityId().toString());
                    cm.put("type", c.summary().entityType().name());
                    cm.put("rank", c.rank());
                    cm.put("retrievalScore", c.retrievalScore());
                    cm.put("summary", parseOrRaw(c.summary().structuredJson()));
                    cm.put("compactText", c.summary().compactText());
                    return cm;
                })
                .toList();
        payload.put("candidates", candidateList);

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to build input JSON for AI prompt", e);
        }
    }

    private Object parseOrRaw(String json) {
        if (json == null) return Map.of();
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            return json;
        }
    }
}
