package com.company.scopery.modules.traceability.dataentity.application.command;

import java.util.List;
import java.util.UUID;

public record ImportFullDataEntityItemCommand(
        UUID applicationId,
        UUID workspaceId,
        UUID moduleId,
        String code,
        String name,
        String description,
        String tableName,
        List<FieldItem> fields) {

    public record FieldItem(
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
            int displayOrder) {}
}
