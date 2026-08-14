package com.company.scopery.modules.traceability.screenspecdoc.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateRegistryScreenSpecDocRequest(
        @NotNull UUID projectId,
        @NotBlank String documentCode,
        @NotBlank String documentName,
        String projectName,
        String systemName,
        String phaseName,
        String language,
        String overview,
        String figmaUrl) {}
