package com.company.scopery.modules.traceability.screen.application.response;

import java.util.List;
import java.util.UUID;

public record ScreenFullSpecResponse(
        UUID id,
        String code,
        String name,
        String routePath,
        String status,
        List<ModeEntry> modes,
        List<SectionEntry> sections,
        List<FieldExportEntry> fields,
        List<ScreenActionEntry> screenActions,
        List<ProcessItemEntry> processItems,
        List<EventItemEntry> eventItems) {

    public record ModeEntry(
            UUID id,
            String modeCode,
            String name,
            int displayOrder,
            String status) {}

    public record SectionEntry(
            UUID id,
            String name,
            String description,
            int displayOrder,
            String status) {}

    public record ScreenActionEntry(
            UUID id,
            String actionCode,
            String name,
            String actionType,
            String description,
            int displayOrder,
            String status) {}

    public record OptionEntry(
            UUID id,
            String optionValue,
            String optionLabel,
            int displayOrder) {}

    public record ComponentSummaryEntry(
            UUID id,
            String code,
            String name,
            String componentType,
            String optionSourceType,
            UUID sourceEntityId,
            String sourceValueColumn,
            String sourceLabelColumn,
            String sourceFilterJson,
            List<OptionEntry> options) {}

    public record DataFieldSummaryEntry(
            UUID id,
            String columnName,
            String dataType,
            Integer maxLength,
            boolean isNullable,
            boolean isUnique) {}

    public record ModeConfigEntry(
            UUID modeId,
            String modeCode,
            boolean isVisible,
            boolean isRequired,
            boolean isReadonly,
            String defaultValue,
            Integer displayOrder) {}

    public record ValidationEntry(
            UUID id,
            UUID modeId,
            String modeCode,
            String ruleTypeCode,
            String ruleParamJson,
            String conditionJson,
            String errorMessage,
            String remark,
            int displayOrder) {}

    public record FieldExportEntry(
            UUID id,
            UUID sectionId,
            String fieldKey,
            String label,
            String fieldType,
            String description,
            boolean required,
            int displayOrder,
            Integer maxLength,
            String remark,
            ComponentSummaryEntry component,
            DataFieldSummaryEntry dataField,
            List<ModeConfigEntry> modeConfigs,
            List<ValidationEntry> validations) {}

    public record ProcessItemEntry(
            UUID id,
            UUID modeId,
            String modeCode,
            UUID targetFieldId,
            String title,
            String content,
            String sourceTable,
            String conditionNote,
            int displayOrder) {}

    public record EventItemEntry(
            UUID id,
            UUID modeId,
            String modeCode,
            UUID triggerFieldId,
            String triggerActionCode,
            String title,
            String content,
            String conditionNote,
            UUID targetScreenId,
            String targetModeCode,
            int displayOrder) {}
}
