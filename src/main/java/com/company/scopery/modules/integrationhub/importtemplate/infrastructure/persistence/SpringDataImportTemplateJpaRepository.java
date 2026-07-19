package com.company.scopery.modules.integrationhub.importtemplate.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List; import java.util.UUID;
public interface SpringDataImportTemplateJpaRepository extends JpaRepository<ImportTemplateJpaEntity, UUID> {
    @Query("SELECT t FROM ImportTemplateJpaEntity t WHERE t.workspaceId = :workspaceId OR t.workspaceId IS NULL")
    List<ImportTemplateJpaEntity> findByWorkspaceIdOrGlobal(@Param("workspaceId") UUID workspaceId);
}
