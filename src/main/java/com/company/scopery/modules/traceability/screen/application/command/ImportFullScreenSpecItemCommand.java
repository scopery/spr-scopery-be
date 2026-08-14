package com.company.scopery.modules.traceability.screen.application.command;

import java.util.List;
import java.util.UUID;

public record ImportFullScreenSpecItemCommand(
        UUID workspaceId,
        UUID applicationId,
        UUID projectId,
        String code,
        String name,
        String routePath,
        List<ModeItem> modes,
        List<FieldItem> fields,
        List<ProcessItem> processItems,
        List<EventItem> eventItems) {

    public record ModeItem(String modeCode, String name, int displayOrder) {}

    public record FieldItem(
            String fieldKey,
            String label,
            String fieldType,
            String description,
            boolean required,
            int displayOrder,
            Integer maxLength,
            String remark,
            String componentCode,
            List<ModeConfigItem> modeConfigs,
            List<ValidationItem> validations) {

        public record ModeConfigItem(
                String modeCode,
                boolean isVisible,
                boolean isRequired,
                boolean isReadonly,
                String defaultValue,
                Integer displayOrder) {}

        public record ValidationItem(
                String modeCode,
                String ruleTypeCode,
                String ruleParamJson,
                String conditionJson,
                String errorMessage,
                String remark,
                int displayOrder) {}
    }

    public record ProcessItem(
            String modeCode,
            String targetFieldKey,
            String title,
            String content,
            String sourceTable,
            String conditionNote,
            int displayOrder) {}

    public record EventItem(
            String modeCode,
            String triggerFieldKey,
            String triggerActionCode,
            String title,
            String content,
            String conditionNote,
            String targetScreenCode,
            String targetModeCode,
            int displayOrder) {}
}
