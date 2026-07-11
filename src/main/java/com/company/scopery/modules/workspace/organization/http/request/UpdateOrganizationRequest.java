package com.company.scopery.modules.workspace.organization.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateOrganizationRequest(
        @NotBlank String name,
        String description) {
}
