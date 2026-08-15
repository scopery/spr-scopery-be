package com.company.scopery.modules.traceability.componentfield.application.command;

import java.util.UUID;

public record CreateRegistryComponentFieldCommand(
        UUID workspaceId, UUID componentId,
        String fieldKey, String label, String fieldType,
        boolean required, Integer maxLength, String remark, int displayOrder) {}
