package com.company.scopery.modules.traceability.appcomponent.application.command;

import java.util.List;
import java.util.UUID;

public record ImportFullAppComponentItemCommand(
        UUID applicationId,
        UUID workspaceId,
        String code,
        String name,
        String description,
        String componentType,
        String optionSourceType,
        UUID sourceEntityId,
        String sourceValueColumn,
        String sourceLabelColumn,
        String sourceFilterJson,
        List<FieldItem> fields) {

    public record FieldItem(
            String fieldKey,
            String label,
            String fieldType,
            boolean required,
            Integer maxLength,
            String remark,
            int displayOrder) {}
}
