package com.company.scopery.modules.airecommendation.infrastructure.port;

import com.company.scopery.modules.airecommendation.application.port.RecommendationEvidenceAccessValidator;
import com.company.scopery.modules.airecommendation.domain.enums.AccessValidationResult;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionEvidence;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AllowAllRecommendationEvidenceAccessValidatorAdapter implements RecommendationEvidenceAccessValidator {

    @Override
    public AccessValidationResult validate(UUID actorId, AiSuggestionEvidence evidence) {
        return AccessValidationResult.ALLOWED;
    }
}
