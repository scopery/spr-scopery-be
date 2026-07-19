package com.company.scopery.modules.configuration.fieldoption.application.command;
import java.util.UUID;
public record CreateFieldOptionCommand(UUID workspaceId, UUID fieldId, String code, String label, Integer sortOrder) {}
