package com.company.scopery.modules.documenthub.comment.application.command;

import java.util.UUID;

public record DeleteCommentCommand(UUID projectId, UUID documentId, UUID commentId) {}
