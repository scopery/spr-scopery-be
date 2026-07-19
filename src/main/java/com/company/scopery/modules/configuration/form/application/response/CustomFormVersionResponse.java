package com.company.scopery.modules.configuration.form.application.response;

import com.company.scopery.modules.configuration.form.domain.model.CustomFormVersion;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Custom form version details")
public record CustomFormVersionResponse(
        @Schema(description = "Unique identifier of the form version", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Identifier of the parent form definition", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID formDefinitionId,

        @Schema(description = "Sequential version number of the form", example = "1")
        int versionNumber,

        @Schema(description = "Current status of the form version",
                allowableValues = {"DRAFT", "PUBLISHED", "ARCHIVED"},
                example = "PUBLISHED")
        String status,

        @Schema(description = "Whether this version is the currently active version", example = "true")
        boolean currentFlag,

        @Schema(description = "Timestamp when this version was published", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant publishedAt) {

    public static CustomFormVersionResponse from(CustomFormVersion v) {
        return new CustomFormVersionResponse(v.id(), v.formDefinitionId(), v.versionNumber(), v.status(), v.currentFlag(), v.publishedAt());
    }
}
