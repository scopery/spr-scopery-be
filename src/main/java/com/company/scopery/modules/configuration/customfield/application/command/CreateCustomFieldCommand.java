package com.company.scopery.modules.configuration.customfield.application.command;
import java.util.UUID;
public record CreateCustomFieldCommand(UUID workspaceId, String objectType, String fieldKey, String label,
                                       String fieldType, Boolean required, Boolean sensitive, Boolean clientVisible) {}
