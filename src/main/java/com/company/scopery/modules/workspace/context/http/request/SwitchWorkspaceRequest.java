package com.company.scopery.modules.workspace.context.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request payload for switching the active workspace for the current user session")
public record SwitchWorkspaceRequest(
        @Schema(description = "ID of the workspace to switch to", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID workspaceId) {}
