package com.company.scopery.modules.workspace.joinrequest.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceJoinRequestRepository {
    WorkspaceJoinRequest save(WorkspaceJoinRequest request);
    Optional<WorkspaceJoinRequest> findById(UUID id);
    Optional<WorkspaceJoinRequest> findPendingByWorkspaceAndUser(UUID workspaceId, UUID userId);
    List<WorkspaceJoinRequest> findByWorkspaceId(UUID workspaceId, String status);
}
