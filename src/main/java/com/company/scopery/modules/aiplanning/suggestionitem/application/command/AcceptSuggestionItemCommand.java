package com.company.scopery.modules.aiplanning.suggestionitem.application.command;

import java.util.UUID;

public record AcceptSuggestionItemCommand(UUID projectId, UUID suggestionId, UUID itemId) {}
