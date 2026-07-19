package com.company.scopery.modules.scope.scopeitem.application.command;

import java.util.UUID;

public record ArchiveScopeItemCommand(
        UUID projectId,
        UUID itemId
) {}
