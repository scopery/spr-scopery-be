package com.company.scopery.modules.iam.ownerpolicy.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateIamOwnerPolicyRequest(
        @NotBlank String resourceType,
        @NotBlank String inheritanceScope,
        boolean canDelegate,
        @Min(0) int delegationDepth,
        @NotEmpty List<@Valid ActionRequest> actions) {
    public record ActionRequest(@NotBlank String permissionCode, @NotBlank String actionCode) {}
}
