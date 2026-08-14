package com.company.scopery.modules.traceability.appcomponent.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record CreateRegistryAppComponentRequest(
        @NotBlank String code,
        @NotBlank String name,
        String description,
        @Schema(description = "Free-form component type label, e.g. SERVICE, CONTROLLER, REPOSITORY, UI_COMPONENT") String componentType,
        @Schema(description = "STATIC, DYNAMIC, or NONE (default)") String optionSourceType,
        @Schema(description = "Required when optionSourceType=DYNAMIC") UUID sourceEntityId,
        @Schema(description = "Column name for option values; required when DYNAMIC") String sourceValueColumn,
        @Schema(description = "Column name for option labels; required when DYNAMIC") String sourceLabelColumn,
        @Schema(description = "Optional filter JSON for DYNAMIC source; op whitelist: IS_NULL, EQUALS, IN") String sourceFilterJson
) {}
