package com.company.scopery.modules.configuration.tag.application.response;

import com.company.scopery.modules.configuration.tag.domain.model.TagAssignment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Tag assignment details linking a tag to a specific object")
public record TagAssignmentResponse(
        @Schema(description = "Unique identifier of the tag assignment", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Identifier of the tag definition that was assigned", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID tagDefinitionId,

        @Schema(description = "Code of the object type the tag is assigned to", example = "PROJECT")
        String objectTypeCode,

        @Schema(description = "Identifier of the target object the tag is assigned to", example = "550e8400-e29b-41d4-a716-446655440002")
        UUID targetId) {

    public static TagAssignmentResponse from(TagAssignment a) {
        return new TagAssignmentResponse(a.id(), a.tagDefinitionId(), a.objectTypeCode(), a.targetId());
    }
}
