package com.company.scopery.modules.iam.authorization.http.request;

import jakarta.validation.constraints.NotBlank;

public record ExplainAuthorizationRequest(
        @NotBlank String permissionCode,
        @NotBlank String actionCode,
        @NotBlank String resourceType,
        @NotBlank String resourceRefId) {
}
