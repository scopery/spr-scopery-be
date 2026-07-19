package com.company.scopery.modules.externalparty.contact.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataExternalContactJpaRepository extends JpaRepository<ExternalContactJpaEntity, UUID> {
    Optional<ExternalContactJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<ExternalContactJpaEntity> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);
}
