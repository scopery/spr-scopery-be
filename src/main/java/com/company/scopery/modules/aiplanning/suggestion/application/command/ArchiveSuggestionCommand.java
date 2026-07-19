package com.company.scopery.modules.aiplanning.suggestion.application.command;

import java.util.UUID;

public record ArchiveSuggestionCommand(UUID projectId, UUID suggestionId) {}
