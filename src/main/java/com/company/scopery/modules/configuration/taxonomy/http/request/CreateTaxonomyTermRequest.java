package com.company.scopery.modules.configuration.taxonomy.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

@Schema(description = "Request payload to create a new taxonomy term within a taxonomy")
public record CreateTaxonomyTermRequest(
        @Schema(description = "Unique code identifying the term within its taxonomy", example = "TECHNOLOGY")
        @NotBlank String termCode,

        @Schema(description = "Display label for the taxonomy term", example = "Technology")
        @NotBlank String label,

        @Schema(description = "Identifier of the parent term (null for root-level terms)", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID parentTermId) {}
