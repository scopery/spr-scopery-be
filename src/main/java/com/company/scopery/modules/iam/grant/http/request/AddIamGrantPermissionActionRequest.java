package com.company.scopery.modules.iam.grant.http.request;

import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AddIamGrantPermissionActionRequest(
        UUID permissionActionId,
        @Size(max = 100) String permissionCode,
        @Size(max = 100) String actionCode) {
}
