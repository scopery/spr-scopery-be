package com.company.scopery.modules.iam.grant.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateIamAccessGrantRequest(
        @NotBlank String subjectType,
        @NotNull UUID subjectId,
        @NotNull UUID resourceId,
        UUID roleId,
        String effect,
        String scopeType,
        UUID scopeRefId,
        UUID workspaceId) {
}
