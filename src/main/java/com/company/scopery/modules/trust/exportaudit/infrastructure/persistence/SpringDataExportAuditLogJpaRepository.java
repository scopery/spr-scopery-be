package com.company.scopery.modules.trust.exportaudit.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataExportAuditLogJpaRepository extends JpaRepository<ExportAuditLogJpaEntity, UUID> {
    List<ExportAuditLogJpaEntity> findByWorkspaceId(UUID workspaceId);
}
