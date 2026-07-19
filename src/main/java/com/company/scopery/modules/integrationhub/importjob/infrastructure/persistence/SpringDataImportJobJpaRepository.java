package com.company.scopery.modules.integrationhub.importjob.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataImportJobJpaRepository extends JpaRepository<ImportJobJpaEntity, UUID> {
    List<ImportJobJpaEntity> findByWorkspaceId(UUID workspaceId);
}
