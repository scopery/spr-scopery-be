package com.company.scopery.modules.collaboration.commentthread.application.command;
import java.util.UUID;
public record ResolveCommentThreadCommand(UUID projectId, UUID threadId) {}
