package com.company.scopery.modules.workspace.orgteam.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request payload for assigning an organization team to a workspace")
public record AssignOrgTeamToWorkspaceRequest(
        @Schema(description = "ID of the workspace to assign the team to", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID workspaceId) {
}
