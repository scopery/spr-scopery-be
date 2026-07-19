package com.company.scopery.modules.aiplanning.suggestionitem.application.command;

import java.util.UUID;

public record RejectSuggestionItemCommand(UUID projectId, UUID suggestionId, UUID itemId) {}
