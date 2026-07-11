package com.company.scopery.modules.iam.permission.application.response;

import com.company.scopery.modules.iam.permission.domain.model.IamPermission;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record IamPermissionResponse(
        UUID id,
        String code,
        String moduleCode,
        String name,
        String description,
        String resourceScopeLevel,
        String dataAccessPolicy,
        String permissionCategory,
        List<String> assignableSubjectTypes,
        String riskLevel,
        String status,
        List<IamPermissionActionResponse> actions,
        Instant createdAt,
        Instant updatedAt) {

    public static IamPermissionResponse from(IamPermission permission,
                                             List<IamPermissionActionResponse> actions) {
        return new IamPermissionResponse(
                permission.id(),
                permission.code().value(),
                permission.moduleCode(),
                permission.name(),
                permission.description(),
                permission.resourceScopeLevel().name(),
                permission.dataAccessPolicy().name(),
                permission.permissionCategory().name(),
                permission.assignableSubjectTypes().stream()
                        .map(Enum::name)
                        .sorted()
                        .toList(),
                permission.riskLevel().name(),
                permission.status().name(),
                List.copyOf(actions),
                permission.createdAt(),
                permission.updatedAt());
    }
}
