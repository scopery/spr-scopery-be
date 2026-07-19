package com.company.scopery.modules.workspace.joinrequest.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Request payload for submitting a join request to a workspace")
public record CreateWorkspaceJoinRequestRequest(
        @Schema(description = "ID of the workspace to join; provide this or workspaceCode", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID workspaceId,

        @Schema(description = "Code of the workspace to join; provide this or workspaceId", example = "ENG-HUB", nullable = true)
        String workspaceCode,

        @Schema(description = "Optional message to the workspace admins explaining the join request", example = "I would like to join to contribute to the backend services.", nullable = true)
        String message) {}
