package com.company.scopery.modules.externalparty.organization.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataExternalOrganizationJpaRepository extends JpaRepository<ExternalOrganizationJpaEntity, UUID> {
    Optional<ExternalOrganizationJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<ExternalOrganizationJpaEntity> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);
}
