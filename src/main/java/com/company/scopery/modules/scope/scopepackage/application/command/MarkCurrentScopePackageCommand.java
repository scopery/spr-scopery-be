package com.company.scopery.modules.scope.scopepackage.application.command;
import java.util.UUID;
public record MarkCurrentScopePackageCommand(UUID projectId, UUID packageId) {}
