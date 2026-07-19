package com.company.scopery.modules.clientportal.comment.application.command;
import java.util.UUID;
public record CreateClientCommentCommand(UUID projectId, String targetType, UUID targetId, String body) {}
