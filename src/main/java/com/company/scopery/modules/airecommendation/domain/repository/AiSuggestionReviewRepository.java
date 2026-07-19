package com.company.scopery.modules.airecommendation.domain.repository;

import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionReview;

import java.util.List;
import java.util.UUID;

public interface AiSuggestionReviewRepository {
    AiSuggestionReview save(AiSuggestionReview review);
    List<AiSuggestionReview> findBySuggestionId(UUID suggestionId);
}
