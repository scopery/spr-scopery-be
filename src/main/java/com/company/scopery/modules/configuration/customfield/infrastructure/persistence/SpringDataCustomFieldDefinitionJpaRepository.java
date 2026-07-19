package com.company.scopery.modules.configuration.customfield.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataCustomFieldDefinitionJpaRepository extends JpaRepository<CustomFieldDefinitionJpaEntity, UUID> {
    Optional<CustomFieldDefinitionJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    boolean existsByWorkspaceIdAndObjectTypeCodeAndFieldKey(UUID workspaceId, String objectTypeCode, String fieldKey);
    List<CustomFieldDefinitionJpaEntity> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);
}
