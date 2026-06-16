package com.company.scopery.modules.workspace.organization.api.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateOrganizationRequest(
        @NotBlank String name,
        String description) {
}
