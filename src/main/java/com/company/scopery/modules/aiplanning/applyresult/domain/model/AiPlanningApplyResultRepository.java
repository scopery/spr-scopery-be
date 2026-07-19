package com.company.scopery.modules.aiplanning.applyresult.domain.model;

import java.util.List;
import java.util.UUID;

public interface AiPlanningApplyResultRepository {
    AiPlanningApplyResult save(AiPlanningApplyResult result);
    List<AiPlanningApplyResult> findBySuggestionId(UUID suggestionId);
}
