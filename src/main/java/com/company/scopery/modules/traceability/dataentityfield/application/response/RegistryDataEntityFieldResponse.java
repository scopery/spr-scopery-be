package com.company.scopery.modules.traceability.dataentityfield.application.response;
import com.company.scopery.modules.traceability.dataentityfield.domain.model.RegistryDataEntityField;
import java.time.Instant; import java.util.UUID;
public record RegistryDataEntityFieldResponse(
        UUID id, UUID entityId, UUID workspaceId,
        String columnName, String dataType, Integer maxLength,
        boolean isNullable, boolean isUnique, boolean isPrimaryKey,
        String defaultValue, Integer precision, Integer scale,
        String remark, int displayOrder, String status, Instant createdAt) {

    public static RegistryDataEntityFieldResponse from(RegistryDataEntityField f) {
        return new RegistryDataEntityFieldResponse(
                f.id(), f.entityId(), f.workspaceId(),
                f.columnName(), f.dataType(), f.maxLength(),
                f.isNullable(), f.isUnique(), f.isPrimaryKey(),
                f.defaultValue(), f.precision(), f.scale(),
                f.remark(), f.displayOrder(), f.status().name(), f.createdAt());
    }
}
