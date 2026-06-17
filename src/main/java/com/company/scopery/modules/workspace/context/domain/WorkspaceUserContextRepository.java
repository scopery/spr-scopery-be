package com.company.scopery.modules.workspace.context.domain;

import java.util.Optional;
import java.util.UUID;

public interface WorkspaceUserContextRepository {
    WorkspaceUserContext save(WorkspaceUserContext context);
    Optional<WorkspaceUserContext> findByUserId(UUID userId);
}
