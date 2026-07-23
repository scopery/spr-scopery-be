package com.company.scopery.modules.traceability.screenfield.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpdateRegistryScreenFieldRequest(@NotBlank String label, @NotBlank String fieldType, String description, boolean required, int displayOrder) {}
