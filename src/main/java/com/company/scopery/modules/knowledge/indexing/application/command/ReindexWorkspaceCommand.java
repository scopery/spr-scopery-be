package com.company.scopery.modules.knowledge.indexing.application.command;

import java.util.UUID;

public record ReindexWorkspaceCommand(UUID workspaceId, UUID requestedByActorId) {}
