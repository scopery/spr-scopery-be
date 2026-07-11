package com.company.scopery.modules.workspace.workspace.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateWorkspaceRequest(
        @NotBlank String name,
        String description,
        String defaultVisibility,
        String joinPolicy) {
}
