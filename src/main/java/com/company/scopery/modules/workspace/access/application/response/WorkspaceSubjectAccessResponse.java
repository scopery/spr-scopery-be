package com.company.scopery.modules.workspace.access.application.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Access information for a single subject (user or team) in a workspace")
public record WorkspaceSubjectAccessResponse(
        @Schema(description = "Type of the subject, e.g. USER or TEAM", example = "USER")
        String subjectType,

        @Schema(description = "ID of the subject", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID subjectId,

        @Schema(description = "Whether the subject is allowed access to the workspace", example = "true")
        boolean allowed,

        @Schema(description = "List of access source descriptions explaining how access was granted")
        List<String> accessSources,

        @Schema(description = "List of IAM grant IDs that contribute to this access")
        List<UUID> contributingGrantIds) {
}
