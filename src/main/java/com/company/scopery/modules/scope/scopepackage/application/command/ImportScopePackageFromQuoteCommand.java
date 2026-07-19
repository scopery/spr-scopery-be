package com.company.scopery.modules.scope.scopepackage.application.command;
import java.util.UUID;
public record ImportScopePackageFromQuoteCommand(UUID projectId, UUID quoteVersionId, String code, String name) {}
