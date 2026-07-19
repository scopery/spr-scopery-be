package com.company.scopery.modules.airecommendation.domain.repository;

import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionImpact;

import java.util.List;
import java.util.UUID;

public interface AiSuggestionImpactRepository {
    AiSuggestionImpact save(AiSuggestionImpact impact);
    List<AiSuggestionImpact> saveAll(List<AiSuggestionImpact> impacts);
    List<AiSuggestionImpact> findBySuggestionId(UUID suggestionId);
}
