package com.company.scopery.modules.iam.permission.domain.model;

import com.company.scopery.modules.iam.permission.domain.enums.IamDataAccessPolicy;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionAssignableSubjectType;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionCategory;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionRiskLevel;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionStatus;
import com.company.scopery.modules.iam.permission.domain.enums.IamResourceScopeLevel;
import com.company.scopery.modules.iam.permission.domain.valueobject.IamPermissionCode;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record IamPermission(
        UUID id,
        IamPermissionCode code,
        String moduleCode,
        String name,
        String description,
        IamResourceScopeLevel resourceScopeLevel,
        IamDataAccessPolicy dataAccessPolicy,
        IamPermissionCategory permissionCategory,
        Set<IamPermissionAssignableSubjectType> assignableSubjectTypes,
        IamPermissionRiskLevel riskLevel,
        IamPermissionStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public IamPermission {
        Objects.requireNonNull(code, "Permission code must not be null");
        Objects.requireNonNull(resourceScopeLevel, "Resource scope level must not be null");
        Objects.requireNonNull(dataAccessPolicy, "Data access policy must not be null");
        Objects.requireNonNull(permissionCategory, "Permission category must not be null");
        Objects.requireNonNull(riskLevel, "Risk level must not be null");
        Objects.requireNonNull(status, "Permission status must not be null");
        if (assignableSubjectTypes == null || assignableSubjectTypes.isEmpty()) {
            throw new IllegalArgumentException("Assignable subject types must not be empty");
        }
        assignableSubjectTypes = Set.copyOf(assignableSubjectTypes);
    }

    public static IamPermission create(IamPermissionCode code, String moduleCode, String name,
                                       String description, IamResourceScopeLevel resourceScopeLevel,
                                       IamDataAccessPolicy dataAccessPolicy,
                                       IamPermissionCategory permissionCategory,
                                       Set<IamPermissionAssignableSubjectType> assignableSubjectTypes,
                                       IamPermissionRiskLevel riskLevel) {
        Instant now = Instant.now();
        return new IamPermission(UUID.randomUUID(), code, normalizeModuleCode(moduleCode),
                name, description, resourceScopeLevel, dataAccessPolicy, permissionCategory,
                assignableSubjectTypes, riskLevel, IamPermissionStatus.ACTIVE, now, now);
    }

    public IamPermission syncCatalog(String moduleCode, String name, String description,
                                     IamResourceScopeLevel resourceScopeLevel,
                                     IamDataAccessPolicy dataAccessPolicy,
                                     IamPermissionCategory permissionCategory,
                                     Set<IamPermissionAssignableSubjectType> assignableSubjectTypes,
                                     IamPermissionRiskLevel riskLevel) {
        return new IamPermission(id, code, normalizeModuleCode(moduleCode), name, description,
                resourceScopeLevel, dataAccessPolicy, permissionCategory, assignableSubjectTypes, riskLevel,
                IamPermissionStatus.ACTIVE, createdAt, Instant.now());
    }

    public IamPermission deactivate() {
        return new IamPermission(id, code, moduleCode, name, description,
                resourceScopeLevel, dataAccessPolicy, permissionCategory, assignableSubjectTypes, riskLevel,
                IamPermissionStatus.INACTIVE, createdAt, Instant.now());
    }

    private static String normalizeModuleCode(String moduleCode) {
        if (moduleCode == null || moduleCode.isBlank()) {
            throw new IllegalArgumentException("Module code must not be blank");
        }
        return moduleCode.trim().toUpperCase();
    }
}
