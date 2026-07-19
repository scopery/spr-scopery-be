package com.company.scopery.modules.knowledge.indexing.application.command;

import java.util.UUID;

public record ReindexProjectCommand(UUID workspaceId, UUID projectId, UUID requestedByActorId) {}
