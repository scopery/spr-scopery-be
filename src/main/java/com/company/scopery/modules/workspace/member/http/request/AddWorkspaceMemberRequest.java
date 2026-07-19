package com.company.scopery.modules.workspace.member.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request payload for adding a member to a workspace")
public record AddWorkspaceMemberRequest(
        @Schema(description = "ID of the user to add as a workspace member", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID userId) {
}
