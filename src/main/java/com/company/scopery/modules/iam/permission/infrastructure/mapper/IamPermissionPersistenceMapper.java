package com.company.scopery.modules.iam.permission.infrastructure.mapper;

import com.company.scopery.modules.iam.permission.domain.enums.IamDataAccessPolicy;
import com.company.scopery.modules.iam.permission.domain.model.IamPermission;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionAssignableSubjectType;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionCategory;
import com.company.scopery.modules.iam.permission.domain.valueobject.IamPermissionCode;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionRiskLevel;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionStatus;
import com.company.scopery.modules.iam.permission.domain.enums.IamResourceScopeLevel;
import com.company.scopery.modules.iam.permission.infrastructure.persistence.IamPermissionJpaEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class IamPermissionPersistenceMapper {

    public IamPermission toDomain(IamPermissionJpaEntity entity) {
        return new IamPermission(
                entity.getId(),
                IamPermissionCode.of(entity.getCode()),
                entity.getModuleCode(),
                entity.getName(),
                entity.getDescription(),
                IamResourceScopeLevel.valueOf(entity.getResourceScopeLevel()),
                IamDataAccessPolicy.valueOf(entity.getDataAccessPolicy()),
                IamPermissionCategory.valueOf(entity.getPermissionCategory()),
                parseAssignableSubjectTypes(entity.getAssignableSubjectTypes()),
                IamPermissionRiskLevel.valueOf(entity.getRiskLevel()),
                IamPermissionStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public IamPermissionJpaEntity toJpaEntity(IamPermission domain) {
        IamPermissionJpaEntity entity = new IamPermissionJpaEntity();
        entity.setId(domain.id());
        entity.setCode(domain.code().value());
        entity.setModuleCode(domain.moduleCode());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setResourceScopeLevel(domain.resourceScopeLevel().name());
        entity.setDataAccessPolicy(domain.dataAccessPolicy().name());
        entity.setPermissionCategory(domain.permissionCategory().name());
        entity.setAssignableSubjectTypes(serializeAssignableSubjectTypes(domain.assignableSubjectTypes()));
        entity.setRiskLevel(domain.riskLevel().name());
        entity.setStatus(domain.status().name());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    private Set<IamPermissionAssignableSubjectType> parseAssignableSubjectTypes(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(token -> !token.isBlank())
                .map(IamPermissionAssignableSubjectType::valueOf)
                .collect(Collectors.toUnmodifiableSet());
    }

    private String serializeAssignableSubjectTypes(Set<IamPermissionAssignableSubjectType> values) {
        return values.stream()
                .sorted(Comparator.comparing(IamPermissionAssignableSubjectType::name))
                .map(IamPermissionAssignableSubjectType::name)
                .collect(Collectors.joining(","));
    }
}
