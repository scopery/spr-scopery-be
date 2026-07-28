package com.company.scopery.modules.workspace.member.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;
import java.util.UUID;

@Schema(description = "Replace a workspace member's project access mode (full workspace vs custom subset)")
public record ReplaceMemberProjectAccessRequest(
        @Schema(description = "Access mode", allowableValues = {"ALL", "CUSTOM"}, example = "CUSTOM")
        @NotBlank
        @Pattern(regexp = "ALL|CUSTOM", message = "mode must be ALL or CUSTOM")
        String mode,

        @Schema(description = "Project IDs to grant when mode=CUSTOM (ignored when mode=ALL). Empty list = shell-only.",
                nullable = true)
        List<UUID> projectIds
) {}
