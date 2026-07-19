package com.company.scopery.modules.configuration.tag.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataTagDefinitionJpaRepository extends JpaRepository<TagDefinitionJpaEntity, UUID> {
    Optional<TagDefinitionJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<TagDefinitionJpaEntity> findByWorkspaceIdOrderByLabelAsc(UUID workspaceId);
    boolean existsByWorkspaceIdAndTagCode(UUID workspaceId, String tagCode);
}
