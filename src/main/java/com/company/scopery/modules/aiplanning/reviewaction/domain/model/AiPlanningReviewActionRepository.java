package com.company.scopery.modules.aiplanning.reviewaction.domain.model;

import java.util.List;
import java.util.UUID;

public interface AiPlanningReviewActionRepository {
    AiPlanningReviewAction save(AiPlanningReviewAction action);
    List<AiPlanningReviewAction> findBySuggestionId(UUID suggestionId);
}
