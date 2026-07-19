package com.company.scopery.modules.configuration.statusset.application.command;
import java.util.UUID;
public record CreateStatusSetCommand(UUID workspaceId, String objectType, String setCode, String name) {}
