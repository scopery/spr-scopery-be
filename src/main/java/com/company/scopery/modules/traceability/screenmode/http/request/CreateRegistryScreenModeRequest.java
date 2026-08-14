package com.company.scopery.modules.traceability.screenmode.http.request;

import jakarta.validation.constraints.NotBlank;

public record CreateRegistryScreenModeRequest(
        @NotBlank String modeCode,
        @NotBlank String name,
        int displayOrder) {}
