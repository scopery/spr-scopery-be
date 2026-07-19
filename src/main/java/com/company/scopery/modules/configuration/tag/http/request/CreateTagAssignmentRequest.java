package com.company.scopery.modules.configuration.tag.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(description = "Request payload to assign a tag to an object")
public record CreateTagAssignmentRequest(
        @Schema(description = "Identifier of the tag definition to assign", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID tagDefinitionId,

        @Schema(description = "Code of the object type the tag is being assigned to", example = "PROJECT")
        @NotBlank String objectTypeCode,

        @Schema(description = "Identifier of the target object to assign the tag to", example = "550e8400-e29b-41d4-a716-446655440001")
        @NotNull UUID targetId) {}
