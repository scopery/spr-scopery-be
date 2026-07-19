package com.company.scopery.modules.documenthub.comment.application.command;

import java.util.UUID;

public record ResolveCommentThreadCommand(UUID projectId, UUID documentId, UUID threadId) {}
