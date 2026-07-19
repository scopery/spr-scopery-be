package com.company.scopery.modules.airecommendation.application.port;

import com.company.scopery.modules.airecommendation.domain.enums.AccessValidationResult;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionEvidence;

import java.util.UUID;

public interface RecommendationEvidenceAccessValidator {
    AccessValidationResult validate(UUID actorId, AiSuggestionEvidence evidence);
}
