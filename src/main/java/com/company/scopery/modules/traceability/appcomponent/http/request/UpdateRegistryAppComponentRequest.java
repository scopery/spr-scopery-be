package com.company.scopery.modules.traceability.appcomponent.http.request;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
public record UpdateRegistryAppComponentRequest(
        @NotBlank String name,
        String description,
        String componentType,
        @Schema(description = "STATIC, DYNAMIC, or NONE") String optionSourceType,
        UUID sourceEntityId,
        String sourceValueColumn,
        String sourceLabelColumn,
        String sourceFilterJson
) {}
