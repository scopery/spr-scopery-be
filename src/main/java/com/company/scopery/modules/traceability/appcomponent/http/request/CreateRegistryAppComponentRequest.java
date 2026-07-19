package com.company.scopery.modules.traceability.appcomponent.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateRegistryAppComponentRequest(@NotBlank String code, @NotBlank String name, String description, String componentType) {}
