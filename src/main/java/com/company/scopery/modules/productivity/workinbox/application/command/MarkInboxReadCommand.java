package com.company.scopery.modules.productivity.workinbox.application.command;
import java.util.UUID;
public record MarkInboxReadCommand(UUID workspaceId, UUID itemId) {}
