package com.company.scopery.modules.configuration.layout.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataLayoutDefinitionJpaRepository extends JpaRepository<LayoutDefinitionJpaEntity, UUID> {
    Optional<LayoutDefinitionJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<LayoutDefinitionJpaEntity> findByWorkspaceIdOrderByNameAsc(UUID workspaceId);
}
