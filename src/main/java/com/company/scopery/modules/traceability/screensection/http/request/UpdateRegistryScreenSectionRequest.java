package com.company.scopery.modules.traceability.screensection.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpdateRegistryScreenSectionRequest(@NotBlank String name, String description, int displayOrder) {}
