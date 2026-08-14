package com.company.scopery.modules.traceability.fieldmodeconfig.application.command;

import java.util.List;
import java.util.UUID;

public record ReplaceScreenFieldModeConfigsCommand(
        UUID workspaceId,
        UUID screenId,
        UUID fieldId,
        List<ModeConfigItem> modeConfigs) {

    public record ModeConfigItem(
            UUID modeId,
            boolean isVisible,
            boolean isRequired,
            boolean isReadonly,
            String defaultValue,
            Integer displayOrder) {}
}
