package com.company.scopery.modules.documenthub.syncedblock.application.command;

import java.util.UUID;

public record UpdateSyncedBlockCommand(UUID workspaceId, UUID syncedBlockId, String ast, Integer schemaVersion) {}
