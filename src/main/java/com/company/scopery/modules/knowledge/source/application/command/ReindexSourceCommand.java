package com.company.scopery.modules.knowledge.source.application.command;

import java.util.UUID;

public record ReindexSourceCommand(UUID sourceId, UUID requestedByActorId) {}
