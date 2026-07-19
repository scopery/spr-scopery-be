package com.company.scopery.modules.configuration.form.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(description = "Request payload to submit a form with field values")
public record SubmitFormRequest(
        @Schema(description = "Identifier of the form version to submit against", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID formVersionId,

        @Schema(description = "Code of the object type the submission is for", example = "PROJECT", nullable = true)
        String objectTypeCode,

        @Schema(description = "Identifier of the target object this submission is for", example = "550e8400-e29b-41d4-a716-446655440001", nullable = true)
        UUID targetId,

        @Schema(description = "Identifier of the project context for this submission", example = "550e8400-e29b-41d4-a716-446655440002", nullable = true)
        UUID projectId,

        @Schema(description = "JSON payload containing the submitted field values", example = "{\"fieldKey\":\"value\"}")
        @NotBlank String payloadJson) {}
