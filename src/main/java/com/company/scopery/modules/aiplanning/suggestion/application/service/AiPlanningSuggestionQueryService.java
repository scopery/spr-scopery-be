package com.company.scopery.modules.aiplanning.suggestion.application.service;

import com.company.scopery.modules.aiplanning.shared.authorization.AiPlanningAuthorizationService;
import com.company.scopery.modules.aiplanning.shared.error.AiPlanningExceptions;
import com.company.scopery.modules.aiplanning.suggestion.application.response.AiPlanningSuggestionResponse;
import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestionRepository;
import com.company.scopery.modules.aiplanning.suggestionitem.application.response.AiPlanningSuggestionItemResponse;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.model.AiPlanningSuggestionItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AiPlanningSuggestionQueryService {
    private final AiPlanningSuggestionRepository suggestions;
    private final AiPlanningSuggestionItemRepository items;
    private final AiPlanningAuthorizationService authorization;

    public AiPlanningSuggestionQueryService(AiPlanningSuggestionRepository suggestions,
                                            AiPlanningSuggestionItemRepository items,
                                            AiPlanningAuthorizationService authorization) {
        this.suggestions = suggestions; this.items = items; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<AiPlanningSuggestionResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return suggestions.findByProjectId(projectId).stream().map(AiPlanningSuggestionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AiPlanningSuggestionResponse get(UUID projectId, UUID suggestionId) {
        authorization.requireView(projectId);
        return suggestions.findByIdAndProjectId(suggestionId, projectId).map(AiPlanningSuggestionResponse::from)
                .orElseThrow(() -> AiPlanningExceptions.suggestionNotFound(suggestionId));
    }

    @Transactional(readOnly = true)
    public List<AiPlanningSuggestionItemResponse> listItems(UUID projectId, UUID suggestionId) {
        authorization.requireView(projectId);
        suggestions.findByIdAndProjectId(suggestionId, projectId)
                .orElseThrow(() -> AiPlanningExceptions.suggestionNotFound(suggestionId));
        return items.findBySuggestionId(suggestionId).stream().map(AiPlanningSuggestionItemResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AiPlanningSuggestionItemResponse getItem(UUID projectId, UUID suggestionId, UUID itemId) {
        authorization.requireView(projectId);
        suggestions.findByIdAndProjectId(suggestionId, projectId)
                .orElseThrow(() -> AiPlanningExceptions.suggestionNotFound(suggestionId));
        return items.findByIdAndSuggestionId(itemId, suggestionId).map(AiPlanningSuggestionItemResponse::from)
                .orElseThrow(() -> AiPlanningExceptions.itemNotFound(itemId));
    }
}
