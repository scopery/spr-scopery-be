package com.company.scopery.modules.airecommendation.application.service;

import com.company.scopery.modules.airecommendation.application.port.RecommendationSourceVersionResolver;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestion;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationStalenessService {

    private final RecommendationSourceVersionResolver versionResolver;

    public RecommendationStalenessService(RecommendationSourceVersionResolver versionResolver) {
        this.versionResolver = versionResolver;
    }

    /**
     * Returns true if the primary target of the suggestion has materially changed since generation.
     * Callers must mark the suggestion STALE when this returns true.
     */
    public boolean isStale(AiSuggestion suggestion) {
        if (suggestion.targetEntityId() == null || suggestion.targetVersionToken() == null) {
            return false;
        }
        String current = versionResolver.resolve(suggestion.targetEntityType(), suggestion.targetEntityId());
        return !suggestion.targetVersionToken().equals(current);
    }

    /**
     * Returns true if any suggestion item's expected target version token has changed.
     * Used before edit and accept operations to detect mid-flight mutations.
     */
    public boolean isItemTargetStale(AiSuggestionItem item) {
        if (item.targetEntityId() == null || item.expectedTargetVersionToken() == null) {
            return false;
        }
        String current = versionResolver.resolve(item.targetEntityType(), item.targetEntityId());
        return !item.expectedTargetVersionToken().equals(current);
    }

    /**
     * Returns true if any of the listed items have a stale target version.
     */
    public boolean anyItemStale(List<AiSuggestionItem> items) {
        return items.stream().anyMatch(this::isItemTargetStale);
    }
}
