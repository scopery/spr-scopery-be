package com.company.scopery.modules.traceability.screenaction.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpdateRegistryScreenActionRequest(@NotBlank String name, @NotBlank String actionType, String description, int displayOrder) {}
