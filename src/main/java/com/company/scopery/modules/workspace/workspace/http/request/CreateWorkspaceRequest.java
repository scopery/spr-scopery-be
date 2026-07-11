package com.company.scopery.modules.workspace.workspace.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateWorkspaceRequest(
        @NotNull UUID organizationId,
        @NotBlank String name,
        @NotBlank String code,
        String description,
        String defaultVisibility,
        String joinPolicy) {
}
