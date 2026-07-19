package com.company.scopery.modules.aiplanning.suggestion.application.command;

import java.util.UUID;

public record StartSuggestionReviewCommand(UUID projectId, UUID suggestionId) {}
