package com.company.scopery.modules.workspace.access.application.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Access summary for a workspace, listing all subjects and their access level")
public record WorkspaceAccessResponse(
        @Schema(description = "ID of the workspace", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID workspaceId,

        @Schema(description = "List of subjects (users or teams) and their access entries for this workspace")
        List<WorkspaceSubjectAccessResponse> subjects) {
}
