package com.company.scopery.modules.workspace.workspace.application.event;

import java.util.UUID;

/**
 * Published after a workspace and its owner membership have been committed.
 * Other modules (e.g. Resource Capacity) listen for this to bootstrap
 * workspace-scoped defaults.
 */
public record WorkspaceCreatedEvent(UUID workspaceId, UUID organizationId, UUID ownerUserId) {
}
