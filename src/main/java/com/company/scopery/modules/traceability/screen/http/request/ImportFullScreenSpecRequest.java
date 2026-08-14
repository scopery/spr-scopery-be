package com.company.scopery.modules.traceability.screen.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record ImportFullScreenSpecRequest(
        @NotNull @Size(min = 1, max = 200) List<@Valid ScreenImportItem> items) {

    public record ScreenImportItem(
            @NotNull UUID projectId,
            @NotBlank @Size(max = 50) String code,
            @NotBlank @Size(max = 255) String name,
            String routePath,
            List<@Valid ModeItem> modes,
            List<@Valid FieldItem> fields,
            List<@Valid ProcessItem> processItems,
            List<@Valid EventItem> eventItems) {

        public record ModeItem(
                @NotBlank String modeCode,
                @NotBlank String name,
                int displayOrder) {}

        public record FieldItem(
                @NotBlank String fieldKey,
                @NotBlank String label,
                String fieldType,
                String description,
                boolean required,
                int displayOrder,
                Integer maxLength,
                String remark,
                String componentCode,
                List<@Valid ModeConfigItem> modeConfigs,
                List<@Valid ValidationItem> validations) {

            public record ModeConfigItem(
                    @NotBlank String modeCode,
                    boolean isVisible,
                    boolean isRequired,
                    boolean isReadonly,
                    String defaultValue,
                    Integer displayOrder) {}

            public record ValidationItem(
                    String modeCode,
                    @NotBlank String ruleTypeCode,
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
                @NotBlank String content,
                String sourceTable,
                String conditionNote,
                int displayOrder) {}

        public record EventItem(
                String modeCode,
                String triggerFieldKey,
                String triggerActionCode,
                String title,
                @NotBlank String content,
                String conditionNote,
                String targetScreenCode,
                String targetModeCode,
                int displayOrder) {}
    }
}
