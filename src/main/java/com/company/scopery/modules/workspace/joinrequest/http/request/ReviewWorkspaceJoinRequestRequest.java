package com.company.scopery.modules.workspace.joinrequest.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for reviewing (approving or rejecting) a workspace join request")
public record ReviewWorkspaceJoinRequestRequest(
        @Schema(description = "Optional note from the reviewer explaining the decision", example = "Welcome to the team!", nullable = true)
        String reviewNote) {}
