package com.company.scopery.modules.integrationhub.mapping.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataDataMappingProfileJpaRepository extends JpaRepository<DataMappingProfileJpaEntity, UUID> {
    List<DataMappingProfileJpaEntity> findByWorkspaceId(UUID workspaceId);
    Optional<DataMappingProfileJpaEntity> findByWorkspaceIdAndMappingCode(UUID workspaceId, String mappingCode);
    boolean existsByWorkspaceIdAndMappingCode(UUID workspaceId, String mappingCode);
}
