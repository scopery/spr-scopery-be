package com.company.scopery.modules.configuration.form.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

@Schema(description = "Request payload to create a new custom form definition")
public record CreateFormRequest(
        @Schema(description = "Unique code identifying the form", example = "PROJECT_CREATE_FORM")
        @NotBlank String formCode,

        @Schema(description = "Display name of the form", example = "Project Creation Form")
        @NotBlank String name,

        @Schema(description = "Code of the object type this form is for", example = "PROJECT")
        @NotBlank String objectTypeCode,

        @Schema(description = "Type of the form", example = "CREATE", nullable = true)
        String formType,

        @Schema(description = "Identifier of the project this form is scoped to", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID projectId) {}
