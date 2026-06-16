package com.company.scopery.modules.workspace.workspace.api.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateWorkspaceRequest(
        @NotBlank String name,
        String description,
        String defaultVisibility) {
}
