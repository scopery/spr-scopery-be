package com.company.scopery.modules.traceability.dataentityfield.domain.model;

import com.company.scopery.modules.traceability.dataentityfield.domain.enums.RegistryDataEntityFieldStatus;

import java.time.Instant;
import java.util.UUID;

public record RegistryDataEntityField(
        UUID id,
        UUID entityId,
        UUID workspaceId,
        String columnName,
        String dataType,
        Integer maxLength,
        boolean isNullable,
        boolean isUnique,
        boolean isPrimaryKey,
        String defaultValue,
        Integer precision,
        Integer scale,
        String remark,
        int displayOrder,
        RegistryDataEntityFieldStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static RegistryDataEntityField create(UUID entityId, UUID workspaceId, String columnName,
                                                  String dataType, Integer maxLength, boolean isNullable,
                                                  boolean isUnique, boolean isPrimaryKey, String defaultValue,
                                                  Integer precision, Integer scale, String remark, int displayOrder) {
        return new RegistryDataEntityField(UUID.randomUUID(), entityId, workspaceId, columnName, dataType,
                maxLength, isNullable, isUnique, isPrimaryKey, defaultValue, precision, scale, remark, displayOrder,
                RegistryDataEntityFieldStatus.ACTIVE, 0, null, null);
    }

    public RegistryDataEntityField withUpdated(String columnName, String dataType, Integer maxLength,
                                                boolean isNullable, boolean isUnique, boolean isPrimaryKey,
                                                String defaultValue, Integer precision, Integer scale,
                                                String remark, int displayOrder) {
        return new RegistryDataEntityField(id, entityId, workspaceId, columnName, dataType, maxLength,
                isNullable, isUnique, isPrimaryKey, defaultValue, precision, scale, remark, displayOrder,
                status, version, createdAt, Instant.now());
    }
}
