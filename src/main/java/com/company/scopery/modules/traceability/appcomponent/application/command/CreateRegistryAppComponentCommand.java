package com.company.scopery.modules.traceability.appcomponent.application.command;
import java.util.UUID;
public record CreateRegistryAppComponentCommand(
        UUID applicationId, UUID workspaceId, String code, String name, String description,
        String componentType, String optionSourceType,
        UUID sourceEntityId, String sourceValueColumn, String sourceLabelColumn, String sourceFilterJson) {}
