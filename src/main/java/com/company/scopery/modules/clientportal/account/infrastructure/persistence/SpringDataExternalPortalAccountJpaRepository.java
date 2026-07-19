package com.company.scopery.modules.clientportal.account.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataExternalPortalAccountJpaRepository extends JpaRepository<ExternalPortalAccountJpaEntity, UUID> {
    Optional<ExternalPortalAccountJpaEntity> findByWorkspaceIdAndEmailIgnoreCase(UUID workspaceId, String email);
}
