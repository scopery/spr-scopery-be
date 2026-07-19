package com.company.scopery.modules.aiplanning.suggestion.application.command;

import java.util.UUID;

public record ApplySuggestionCommand(
        UUID projectId,
        UUID suggestionId,
        String applyMode,
        Boolean requireChangeRequestIfBaselined
) {}
