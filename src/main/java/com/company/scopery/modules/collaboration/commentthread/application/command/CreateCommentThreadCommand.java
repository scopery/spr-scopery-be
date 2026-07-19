package com.company.scopery.modules.collaboration.commentthread.application.command;
import java.util.UUID;
public record CreateCommentThreadCommand(UUID projectId, String targetType, UUID targetId, String title, Boolean clientVisible) {}
