package com.company.scopery.modules.scope.scopepackage.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateScopePackageRequest(@NotBlank String code, @NotBlank String name, String description) {}
