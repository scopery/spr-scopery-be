package com.company.scopery.modules.workspace.organization.api.request;

import jakarta.validation.constraints.NotBlank;

public record CreateOrganizationRequest(
        @NotBlank String name,
        @NotBlank String code,
        String description) {
}
