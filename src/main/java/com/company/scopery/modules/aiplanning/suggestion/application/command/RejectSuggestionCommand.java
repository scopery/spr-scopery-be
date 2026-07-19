package com.company.scopery.modules.aiplanning.suggestion.application.command;

import java.util.UUID;

public record RejectSuggestionCommand(UUID projectId, UUID suggestionId, String reason) {}
