package com.company.scopery.modules.configuration.taxonomy.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload to create a new taxonomy")
public record CreateTaxonomyRequest(
        @Schema(description = "Unique code identifying the taxonomy", example = "INDUSTRY_CLASSIFICATION")
        @NotBlank String taxonomyCode,

        @Schema(description = "Display name of the taxonomy", example = "Industry Classification")
        @NotBlank String name) {}
