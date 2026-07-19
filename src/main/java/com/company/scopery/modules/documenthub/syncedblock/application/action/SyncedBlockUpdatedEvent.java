package com.company.scopery.modules.documenthub.syncedblock.application.action;

import java.util.UUID;

public record SyncedBlockUpdatedEvent(UUID syncedBlockId, String newAst) {}
