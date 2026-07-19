package com.company.scopery.modules.scope.scopeitem.application.command;

import java.util.UUID;

public record UpdateScopeItemCommand(
        UUID projectId,
        UUID itemId,
        String title,
        String description,
        Boolean inScope,
        Boolean outOfScope,
        String priority,
        Boolean acceptanceRequired,
        Integer sortOrder
) {}
