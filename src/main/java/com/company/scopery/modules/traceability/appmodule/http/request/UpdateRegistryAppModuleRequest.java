package com.company.scopery.modules.traceability.appmodule.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpdateRegistryAppModuleRequest(@NotBlank String name, String description) {}
