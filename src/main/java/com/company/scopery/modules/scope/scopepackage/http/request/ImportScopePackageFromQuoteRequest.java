package com.company.scopery.modules.scope.scopepackage.http.request;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record ImportScopePackageFromQuoteRequest(@NotNull UUID quoteVersionId, String code, String name) {}
