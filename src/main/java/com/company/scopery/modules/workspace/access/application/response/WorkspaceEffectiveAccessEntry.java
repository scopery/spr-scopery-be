package com.company.scopery.modules.workspace.access.application.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "A single workspace entry in a subject's effective access list")
public record WorkspaceEffectiveAccessEntry(
        @Schema(description = "ID of the workspace", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID workspaceId,

        @Schema(description = "Unique short code of the workspace", example = "ENG-HUB")
        String workspaceCode,

        @Schema(description = "Display name of the workspace", example = "Engineering Hub")
        String workspaceName,

        @Schema(description = "List of access source descriptions (e.g. direct membership, team assignment)")
        List<String> accessSources,

        @Schema(description = "List of IAM grant IDs that contribute to this access")
        List<UUID> contributingGrantIds) {
}
