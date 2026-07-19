package com.company.scopery.modules.integrationhub.exportjob.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataExportJobJpaRepository extends JpaRepository<ExportJobJpaEntity, UUID> {
    List<ExportJobJpaEntity> findByWorkspaceId(UUID workspaceId);
}
