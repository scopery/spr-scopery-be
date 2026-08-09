package com.company.scopery.modules.elicitation.suggestion.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ElicitationSuggestionRepository {

    ElicitationSuggestion save(ElicitationSuggestion suggestion);

    Optional<ElicitationSuggestion> findById(UUID id);

    Optional<ElicitationSuggestion> findByRoundId(UUID roundId);

    ElicitationSuggestionItem saveItem(ElicitationSuggestionItem item);

    List<ElicitationSuggestionItem> saveAllItems(List<ElicitationSuggestionItem> items);

    Optional<ElicitationSuggestionItem> findItemById(UUID itemId);

    List<ElicitationSuggestionItem> findAllItemsBySuggestionId(UUID suggestionId);
}
