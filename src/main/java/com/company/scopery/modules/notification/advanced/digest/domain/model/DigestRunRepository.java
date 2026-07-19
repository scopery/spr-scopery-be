package com.company.scopery.modules.notification.advanced.digest.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface DigestRunRepository {
    DigestRun save(DigestRun r);
    Optional<DigestRun> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<DigestRun> findByWorkspaceId(UUID workspaceId);
    List<DigestRun> findByWorkspaceIdAndRecipientUserId(UUID workspaceId, UUID recipientUserId);
}
