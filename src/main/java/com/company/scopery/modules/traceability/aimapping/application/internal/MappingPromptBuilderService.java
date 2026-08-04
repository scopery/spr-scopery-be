package com.company.scopery.modules.traceability.aimapping.application.internal;

import com.company.scopery.modules.traceability.aimapping.application.internal.MappingCandidateRetrievalService.UnmappedSource;
import com.company.scopery.modules.traceability.aimapping.summary.domain.model.MappingSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MappingPromptBuilderService {

    private final ObjectMapper objectMapper;

    public MappingPromptBuilderService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public record ScoredCandidate(MappingSummary summary, double retrievalScore, int rank, double titleSimilarity) {}

    /** Single-source input (retained for backward-compat / tests). */
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

    /**
     * Batch input format used by the V2 prompts.
     * Shape: { "sources": [...], "candidatesPerSource": { "uuid": [...] } }
     */
    public String buildBatchInputJson(List<UnmappedSource> sources,
                                       Map<UUID, MappingSummary> sourceSummaries,
                                       Map<UUID, List<ScoredCandidate>> candidatesPerSource) {
        Map<String, Object> payload = new LinkedHashMap<>();

        List<Map<String, Object>> sourceList = sources.stream()
                .filter(s -> sourceSummaries.containsKey(s.id()))
                .map(s -> {
                    MappingSummary summary = sourceSummaries.get(s.id());
                    Map<String, Object> sm = new LinkedHashMap<>();
                    sm.put("id", s.id().toString());
                    sm.put("type", s.sourceType().name());
                    sm.put("summary", parseOrRaw(summary.structuredJson()));
                    sm.put("compactText", summary.compactText());
                    return sm;
                })
                .toList();
        payload.put("sources", sourceList);

        Map<String, Object> candidatesMap = new LinkedHashMap<>();
        for (Map.Entry<UUID, List<ScoredCandidate>> entry : candidatesPerSource.entrySet()) {
            List<Map<String, Object>> candList = entry.getValue().stream()
                    .map(c -> {
                        Map<String, Object> cm = new LinkedHashMap<>();
                        cm.put("id", c.summary().entityId().toString());
                        cm.put("type", c.summary().entityType().name());
                        cm.put("summary", parseOrRaw(c.summary().structuredJson()));
                        cm.put("compactText", c.summary().compactText());
                        return cm;
                    })
                    .toList();
            candidatesMap.put(entry.getKey().toString(), candList);
        }
        payload.put("candidatesPerSource", candidatesMap);

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to build batch input JSON for AI prompt", e);
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
