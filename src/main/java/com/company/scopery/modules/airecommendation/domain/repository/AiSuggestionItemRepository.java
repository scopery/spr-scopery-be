package com.company.scopery.modules.airecommendation.domain.repository;

import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiSuggestionItemRepository {
    AiSuggestionItem save(AiSuggestionItem item);
    List<AiSuggestionItem> saveAll(List<AiSuggestionItem> items);
    Optional<AiSuggestionItem> findById(UUID id);
    List<AiSuggestionItem> findBySuggestionId(UUID suggestionId);
}
