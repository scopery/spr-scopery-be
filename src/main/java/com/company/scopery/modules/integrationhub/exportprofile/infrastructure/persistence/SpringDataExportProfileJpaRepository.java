package com.company.scopery.modules.integrationhub.exportprofile.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataExportProfileJpaRepository extends JpaRepository<ExportProfileJpaEntity, UUID> {
    List<ExportProfileJpaEntity> findByWorkspaceId(UUID workspaceId);
    Optional<ExportProfileJpaEntity> findByWorkspaceIdAndProfileCode(UUID workspaceId, String profileCode);
    boolean existsByWorkspaceIdAndProfileCode(UUID workspaceId, String profileCode);
}
