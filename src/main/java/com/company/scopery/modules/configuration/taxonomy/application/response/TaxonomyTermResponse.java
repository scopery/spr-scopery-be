package com.company.scopery.modules.configuration.taxonomy.application.response;

import com.company.scopery.modules.configuration.taxonomy.domain.model.TaxonomyTerm;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Taxonomy term details representing a node in a taxonomy hierarchy")
public record TaxonomyTermResponse(
        @Schema(description = "Unique identifier of the taxonomy term", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Identifier of the taxonomy this term belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID taxonomyId,

        @Schema(description = "Identifier of the parent term (null for root-level terms)", example = "550e8400-e29b-41d4-a716-446655440002", nullable = true)
        UUID parentTermId,

        @Schema(description = "Unique code identifying the term within its taxonomy", example = "TECHNOLOGY")
        String termCode,

        @Schema(description = "Display label for the taxonomy term", example = "Technology")
        String label) {

    public static TaxonomyTermResponse from(TaxonomyTerm t) {
        return new TaxonomyTermResponse(t.id(), t.taxonomyId(), t.parentTermId(), t.termCode(), t.label());
    }
}
