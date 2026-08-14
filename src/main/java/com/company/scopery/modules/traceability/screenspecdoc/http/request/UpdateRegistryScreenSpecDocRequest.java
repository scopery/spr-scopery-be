package com.company.scopery.modules.traceability.screenspecdoc.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateRegistryScreenSpecDocRequest(
        @NotBlank String documentName,
        String projectName,
        String systemName,
        String phaseName,
        String language,
        String overview,
        String figmaUrl) {}
