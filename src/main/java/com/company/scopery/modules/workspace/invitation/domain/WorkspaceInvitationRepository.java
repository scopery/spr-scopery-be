package com.company.scopery.modules.workspace.invitation.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceInvitationRepository {
    WorkspaceInvitation save(WorkspaceInvitation invitation);
    Optional<WorkspaceInvitation> findById(UUID id);
    Optional<WorkspaceInvitation> findByCodeHash(String codeHash);
    List<WorkspaceInvitation> findByWorkspaceId(UUID workspaceId);
}
