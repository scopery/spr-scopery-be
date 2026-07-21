package com.company.scopery.modules.aiaction.infrastructure.stub;

import com.company.scopery.modules.aiaction.application.port.AiActionPhase43SuggestionPort;
import com.company.scopery.modules.aiaction.application.port.AiActionSuggestionData;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import org.springframework.stereotype.Component;

// Stub implementation — replaced in Step 20
@Component
public class StubAiActionPhase43SuggestionPort implements AiActionPhase43SuggestionPort {

    @Override
    public AiActionSuggestionData resolve(String suggestionRef) {
        throw AiActionExceptions.domainCapabilityUnavailable("Phase 43 suggestion resolution not yet implemented");
    }

    @Override
    public boolean isValidAndApplicable(String suggestionRef) {
        return false;
    }
}
