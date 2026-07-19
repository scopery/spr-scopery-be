package com.company.scopery.modules.configuration.tag.application.command;
import java.util.UUID;
public record CreateTagCommand(UUID workspaceId, String code, String label, String color, String allowed) {}
