package com.company.scopery.modules.collaboration.comment.application.command;
import java.util.UUID;
public record UpdateCommentCommand(UUID projectId, UUID commentId, String body) {}
