package com.company.scopery.modules.notification.advanced.digest.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataDigestRunJpaRepository extends JpaRepository<DigestRunJpaEntity, UUID> {
    Optional<DigestRunJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<DigestRunJpaEntity> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);
    List<DigestRunJpaEntity> findByWorkspaceIdAndRecipientUserIdOrderByCreatedAtDesc(UUID workspaceId, UUID recipientUserId);
}
