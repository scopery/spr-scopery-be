package com.company.scopery.modules.scope.scopeitem.application.command;

import java.util.UUID;

public record CreateScopeItemCommand(
        UUID projectId,
        UUID packageId,
        String type,
        String code,
        String title,
        String description,
        Boolean inScope,
        Boolean outOfScope,
        String priority,
        Boolean acceptanceRequired,
        Integer sortOrder
) {}
