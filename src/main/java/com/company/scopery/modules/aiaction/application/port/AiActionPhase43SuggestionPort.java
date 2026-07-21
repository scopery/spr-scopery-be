package com.company.scopery.modules.aiaction.application.port;

public interface AiActionPhase43SuggestionPort {

    AiActionSuggestionData resolve(String suggestionRef);

    boolean isValidAndApplicable(String suggestionRef);
}
