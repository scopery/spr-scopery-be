package com.company.scopery.modules.airecommendation.application.service;

import com.company.scopery.modules.airecommendation.application.query.GetNextBestActionsQuery;
import com.company.scopery.modules.airecommendation.application.response.NextBestActionItemResponse;
import com.company.scopery.modules.airecommendation.domain.enums.NbaStatus;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestion;
import com.company.scopery.modules.airecommendation.domain.model.NextBestActionDefinition;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionRepository;
import com.company.scopery.modules.airecommendation.domain.repository.NextBestActionDefinitionRepository;
import com.company.scopery.modules.airecommendation.domain.value.SuggestionRef;
import com.company.scopery.modules.airecommendation.shared.error.AiRecommendationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class NextBestActionQueryService {

    private final NextBestActionDefinitionRepository nbaRepository;
    private final AiSuggestionRepository suggestionRepository;

    public NextBestActionQueryService(NextBestActionDefinitionRepository nbaRepository,
                                       AiSuggestionRepository suggestionRepository) {
        this.nbaRepository = nbaRepository;
        this.suggestionRepository = suggestionRepository;
    }

    @Transactional(readOnly = true)
    public List<NextBestActionItemResponse> listNextBestActions(GetNextBestActionsQuery q) {
        AiSuggestion suggestion = null;
        if (q.suggestionRef() != null) {
            SuggestionRef ref = SuggestionRef.parse(q.suggestionRef());
            suggestion = suggestionRepository.findById(ref.uuid())
                    .orElseThrow(() -> AiRecommendationExceptions.suggestionNotFound(q.suggestionRef()));
        }

        List<NextBestActionDefinition> allActive = nbaRepository.findAllActive();
        final AiSuggestion finalSuggestion = suggestion;

        return allActive.stream()
                .filter(nba -> nba.status() == NbaStatus.ACTIVE)
                .filter(nba -> isApplicable(nba, finalSuggestion))
                .limit(q.limit() > 0 ? q.limit() : Long.MAX_VALUE)
                .map(nba -> toResponse(nba, finalSuggestion))
                .toList();
    }

    private boolean isApplicable(NextBestActionDefinition nba, AiSuggestion suggestion) {
        if (suggestion == null) return true;
        List<String> types = nba.applicableSuggestionTypes();
        if (types == null || types.isEmpty()) return true;
        return types.contains(suggestion.suggestionType());
    }

    private NextBestActionItemResponse toResponse(NextBestActionDefinition nba, AiSuggestion suggestion) {
        boolean enabled = suggestion != null && isEnabled(nba, suggestion);
        String disabledReasonCode = enabled ? null : deriveDisabledReason(nba, suggestion);

        return new NextBestActionItemResponse(
                nba.code(), nba.label(),
                nba.actionKind() != null ? nba.actionKind().name() : null,
                enabled, nba.riskLevel(), nba.requiredAuthorityCode(),
                nba.phase44ToolCode(), disabledReasonCode,
                Map.of());
    }

    private boolean isEnabled(NextBestActionDefinition nba, AiSuggestion suggestion) {
        if (suggestion == null) return false;
        // RESERVED_PHASE44 actions are always disabled
        if (nba.status() == NbaStatus.RESERVED_PHASE44) return false;
        // Phase 44 tool requires Phase 44 to be enabled (not yet)
        if (nba.phase44ToolCode() != null && !nba.phase44ToolCode().isBlank()) return false;
        return true;
    }

    private String deriveDisabledReason(NextBestActionDefinition nba, AiSuggestion suggestion) {
        if (nba.status() == NbaStatus.RESERVED_PHASE44) return "PHASE44_NOT_ENABLED";
        if (nba.phase44ToolCode() != null && !nba.phase44ToolCode().isBlank()) return "PHASE44_NOT_ENABLED";
        return "UNAVAILABLE";
    }
}
