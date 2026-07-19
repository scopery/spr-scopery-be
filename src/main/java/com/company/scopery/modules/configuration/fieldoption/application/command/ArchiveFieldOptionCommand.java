package com.company.scopery.modules.configuration.fieldoption.application.command;
import java.util.UUID;
public record ArchiveFieldOptionCommand(UUID workspaceId, UUID fieldId, UUID optionId) {}
