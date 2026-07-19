package com.company.scopery.modules.configuration.form.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataCustomFormDefinitionJpaRepository extends JpaRepository<CustomFormDefinitionJpaEntity, UUID> {
    Optional<CustomFormDefinitionJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<CustomFormDefinitionJpaEntity> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);
    boolean existsByWorkspaceIdAndFormCode(UUID workspaceId, String formCode);
}
