package com.company.scopery.modules.traceability.appmodule.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateRegistryAppModuleRequest(@NotBlank String code, @NotBlank String name, String description) {}
