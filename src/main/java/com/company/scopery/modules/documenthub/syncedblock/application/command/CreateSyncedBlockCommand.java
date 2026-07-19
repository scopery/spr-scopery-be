package com.company.scopery.modules.documenthub.syncedblock.application.command;

import java.util.UUID;

public record CreateSyncedBlockCommand(UUID workspaceId, UUID projectId, String title, String ast, Integer schemaVersion) {}
