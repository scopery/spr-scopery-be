package com.company.scopery.modules.traceability.screenmode.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateRegistryScreenModeRequest(
        @NotBlank String name,
        int displayOrder) {}
