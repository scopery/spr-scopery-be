package com.company.scopery.modules.traceability.dataentityfield.application.command;
import java.util.UUID;
public record UpdateRegistryDataEntityFieldCommand(
        UUID workspaceId, UUID fieldId,
        String columnName, String dataType, Integer maxLength,
        boolean isNullable, boolean isUnique, boolean isPrimaryKey,
        String defaultValue, Integer precision, Integer scale,
        String remark, int displayOrder) {}
