package com.company.scopery.modules.integrationhub.mapping.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataExternalIdMappingJpaRepository extends JpaRepository<ExternalIdMappingJpaEntity, UUID> {
    List<ExternalIdMappingJpaEntity> findByWorkspaceId(UUID workspaceId);
    List<ExternalIdMappingJpaEntity> findByWorkspaceIdAndScoperyObjectTypeAndScoperyObjectId(UUID workspaceId, String type, UUID objectId);
}
