package com.company.scopery.modules.traceability.fieldmodeconfig.application.command;

import java.util.UUID;

public record DeleteScreenFieldModeConfigCommand(UUID workspaceId, UUID configId) {}
