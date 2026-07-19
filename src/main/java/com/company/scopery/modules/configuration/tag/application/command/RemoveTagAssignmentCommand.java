package com.company.scopery.modules.configuration.tag.application.command;
import java.util.UUID;
public record RemoveTagAssignmentCommand(UUID workspaceId, UUID assignmentId) {}
