package com.company.scopery.modules.scope.scopepackage.application.command;

import java.util.UUID;

public record CreateScopePackageCommand(
        UUID projectId,
        String code,
        String name,
        String description
) {}
