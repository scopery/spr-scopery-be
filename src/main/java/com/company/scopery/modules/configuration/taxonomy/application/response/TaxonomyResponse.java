package com.company.scopery.modules.configuration.taxonomy.application.response;

import com.company.scopery.modules.configuration.taxonomy.domain.model.Taxonomy;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Taxonomy details representing a hierarchical classification system")
public record TaxonomyResponse(
        @Schema(description = "Unique identifier of the taxonomy", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Unique code identifying the taxonomy", example = "INDUSTRY_CLASSIFICATION")
        String taxonomyCode,

        @Schema(description = "Display name of the taxonomy", example = "Industry Classification")
        String name,

        @Schema(description = "Current status of the taxonomy",
                allowableValues = {"ACTIVE", "ARCHIVED"},
                example = "ACTIVE")
        String status) {

    public static TaxonomyResponse from(Taxonomy t) {
        return new TaxonomyResponse(t.id(), t.taxonomyCode(), t.name(), t.status());
    }
}
