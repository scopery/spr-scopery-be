package com.company.scopery.modules.clientportal.auditlog.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataExternalPortalAuditLogJpaRepository extends JpaRepository<ExternalPortalAuditLogJpaEntity, UUID> {
    List<ExternalPortalAuditLogJpaEntity> findByProjectId(UUID projectId);
    List<ExternalPortalAuditLogJpaEntity> findByWorkspaceIdAndPortalAccountId(UUID workspaceId, UUID portalAccountId);
}
