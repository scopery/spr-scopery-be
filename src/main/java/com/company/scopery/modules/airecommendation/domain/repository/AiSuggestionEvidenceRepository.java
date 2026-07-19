package com.company.scopery.modules.airecommendation.domain.repository;

import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionEvidence;

import java.util.List;
import java.util.UUID;

public interface AiSuggestionEvidenceRepository {
    AiSuggestionEvidence save(AiSuggestionEvidence evidence);
    List<AiSuggestionEvidence> saveAll(List<AiSuggestionEvidence> evidences);
    List<AiSuggestionEvidence> findBySuggestionId(UUID suggestionId);
    boolean existsBySuggestionAndSourceKey(UUID suggestionId, String sourceType, UUID sourceRefId, UUID knowledgeChunkId);
}
