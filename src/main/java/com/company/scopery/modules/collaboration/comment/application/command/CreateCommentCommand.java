package com.company.scopery.modules.collaboration.comment.application.command;
import java.util.List; import java.util.UUID;
public record CreateCommentCommand(UUID projectId, UUID threadId, UUID parentId, String body, Boolean clientVisible, List<UUID> mentionUserIds) {}
