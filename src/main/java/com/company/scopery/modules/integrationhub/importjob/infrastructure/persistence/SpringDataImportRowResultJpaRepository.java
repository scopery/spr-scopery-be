package com.company.scopery.modules.integrationhub.importjob.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataImportRowResultJpaRepository extends JpaRepository<ImportRowResultJpaEntity, UUID> {
    List<ImportRowResultJpaEntity> findByImportJobIdOrderByRowNumber(UUID importJobId);
    List<ImportRowResultJpaEntity> findByWorkspaceIdAndImportJobIdOrderByRowNumber(UUID workspaceId, UUID importJobId);
}
