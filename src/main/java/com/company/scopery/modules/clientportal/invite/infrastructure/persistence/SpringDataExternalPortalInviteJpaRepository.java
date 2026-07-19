package com.company.scopery.modules.clientportal.invite.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataExternalPortalInviteJpaRepository extends JpaRepository<ExternalPortalInviteJpaEntity, UUID> {
    Optional<ExternalPortalInviteJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    Optional<ExternalPortalInviteJpaEntity> findByInviteTokenHash(String inviteTokenHash);
    List<ExternalPortalInviteJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
