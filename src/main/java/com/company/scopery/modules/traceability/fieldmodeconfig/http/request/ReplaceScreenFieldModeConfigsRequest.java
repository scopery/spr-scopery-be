package com.company.scopery.modules.traceability.fieldmodeconfig.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ReplaceScreenFieldModeConfigsRequest(
        @NotNull @Valid List<ModeConfigItemRequest> modeConfigs) {

    public record ModeConfigItemRequest(
            @NotNull UUID modeId,
            boolean isVisible,
            boolean isRequired,
            boolean isReadonly,
            String defaultValue,
            Integer displayOrder) {}
}
