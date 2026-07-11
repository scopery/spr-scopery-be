package com.company.scopery.modules.iam.grant.http.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DelegateIamAccessRequest(
        @NotBlank String subjectType,
        @NotNull UUID subjectId,
        @NotBlank String resourceType,
        @NotNull UUID resourceRefId,
        @Min(0) int delegationDepth,
        @Future Instant expiresAt,
        JsonNode condition,
        String reason,
        @NotEmpty List<@Valid PermissionActionRequest> actions) {
    public record PermissionActionRequest(@NotBlank String permissionCode, @NotBlank String actionCode) {}
}
