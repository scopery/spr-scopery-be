package com.company.scopery.modules.iam.access.http.request;

import jakarta.validation.constraints.NotBlank;

public record ExplainAccessRequest(
        @NotBlank String permissionCode,
        @NotBlank String actionCode,
        @NotBlank String resourceType,
        @NotBlank String resourceRefId) {
}
