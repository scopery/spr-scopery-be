package com.company.scopery.modules.collaboration.comment.application.command;
import java.util.UUID;
public record DeleteCommentCommand(UUID projectId, UUID commentId) {}
