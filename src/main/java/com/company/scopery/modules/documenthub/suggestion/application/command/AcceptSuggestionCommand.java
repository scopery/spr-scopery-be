package com.company.scopery.modules.documenthub.suggestion.application.command;

import java.util.UUID;

public record AcceptSuggestionCommand(UUID projectId, UUID documentId, UUID suggestionId) {}
