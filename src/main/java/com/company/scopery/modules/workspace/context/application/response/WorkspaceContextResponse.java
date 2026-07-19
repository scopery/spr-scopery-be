package com.company.scopery.modules.workspace.context.application.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "The current workspace context for an authenticated user")
public record WorkspaceContextResponse(
        @Schema(description = "ID of the authenticated user", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID userId,

        @Schema(description = "ID of the user's currently active workspace", example = "550e8400-e29b-41d4-a716-446655440001", nullable = true)
        UUID currentWorkspaceId,

        @Schema(description = "Display name of the currently active workspace", example = "Engineering Hub", nullable = true)
        String currentWorkspaceName,

        @Schema(description = "Short code of the currently active workspace", example = "ENG-HUB", nullable = true)
        String currentWorkspaceCode,

        @Schema(description = "Timestamp when the user last switched their active workspace", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant lastSwitchedAt,

        @Schema(description = "Whether the user has completed onboarding", example = "true")
        boolean onboardingCompleted) {}
