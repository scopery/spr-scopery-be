package com.company.scopery.modules.traceability.dataentityfield.application.command;

import java.util.UUID;

public record CreateRegistryDataEntityFieldCommand(
        UUID entityId,
        UUID workspaceId,
        String columnName,
        String dataType,
        Integer maxLength,
        boolean isNullable,
        boolean isUnique,
        String remark,
        int displayOrder) {}
