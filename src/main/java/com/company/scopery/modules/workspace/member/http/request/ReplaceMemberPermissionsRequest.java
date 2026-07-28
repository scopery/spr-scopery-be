package com.company.scopery.modules.workspace.member.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ReplaceMemberPermissionsRequest(
        @NotNull List<UUID> permissionActionIds
) {}
