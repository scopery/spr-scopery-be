package com.company.scopery.modules.iam.authorization.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CheckAccessRequest(
        @NotNull UUID userId,
        @NotNull UUID resourceId,
        @NotBlank String rightCode) {
}
