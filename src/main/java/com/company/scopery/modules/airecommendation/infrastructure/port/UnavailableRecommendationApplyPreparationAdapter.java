package com.company.scopery.modules.airecommendation.infrastructure.port;

import com.company.scopery.modules.airecommendation.application.port.RecommendationApplyPreparationPort;
import org.springframework.stereotype.Component;

@Component
public class UnavailableRecommendationApplyPreparationAdapter implements RecommendationApplyPreparationPort {

    @Override
    public PrepareApplyResult prepare(PrepareApplyRequest request) {
        return PrepareApplyResult.unavailable(request.suggestionRef());
    }
}
