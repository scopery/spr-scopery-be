package com.company.scopery.modules.iam.role.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateIamRoleRequest(
        @NotBlank @Size(min = 2, max = 100) String code,
        @NotBlank @Size(max = 255) String name,
        @Size(max = 2000) String description,
        String roleScope,
        String roleSource,
        UUID workspaceId,
        UUID parentRoleId) {}
