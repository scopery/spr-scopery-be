package com.company.scopery.modules.projectbaseline.baseline.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpdateBaselineRequest(@NotBlank String name, String description) {}
