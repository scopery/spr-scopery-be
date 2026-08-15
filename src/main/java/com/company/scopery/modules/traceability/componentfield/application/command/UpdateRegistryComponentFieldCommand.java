package com.company.scopery.modules.traceability.componentfield.application.command;

import java.util.UUID;

public record UpdateRegistryComponentFieldCommand(
        UUID workspaceId, UUID fieldId,
        String label, String fieldType,
        boolean required, Integer maxLength, String remark, int displayOrder) {}
