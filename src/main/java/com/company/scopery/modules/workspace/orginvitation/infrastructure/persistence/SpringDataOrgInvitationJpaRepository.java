package com.company.scopery.modules.workspace.orginvitation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataOrgInvitationJpaRepository
        extends JpaRepository<OrgInvitationJpaEntity, UUID> {

    Optional<OrgInvitationJpaEntity> findByTokenHash(String tokenHash);

    List<OrgInvitationJpaEntity> findByInviteeEmailIgnoreCaseAndStatusOrderByCreatedAtDesc(
            String inviteeEmail, String status);
}
