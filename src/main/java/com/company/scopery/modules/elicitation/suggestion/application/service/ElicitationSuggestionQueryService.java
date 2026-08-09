package com.company.scopery.modules.elicitation.suggestion.application.service;

import com.company.scopery.modules.elicitation.suggestion.application.response.ElicitationSuggestionResponse;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestion;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionItem;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionRepository;
import com.company.scopery.modules.elicitation.shared.error.ElicitationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ElicitationSuggestionQueryService {

    private final ElicitationSuggestionRepository suggestionRepository;

    public ElicitationSuggestionQueryService(ElicitationSuggestionRepository suggestionRepository) {
        this.suggestionRepository = suggestionRepository;
    }

    @Transactional(readOnly = true)
    public ElicitationSuggestionResponse getByRoundId(UUID roundId) {
        ElicitationSuggestion suggestion = suggestionRepository.findByRoundId(roundId)
                .orElseThrow(() -> ElicitationExceptions.suggestionNotFound(roundId));
        List<ElicitationSuggestionItem> items = suggestionRepository.findAllItemsBySuggestionId(suggestion.id());
        return ElicitationSuggestionResponse.from(suggestion, items);
    }
}
