package com.company.scopery.modules.configuration.fieldvalue.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataCustomFieldValueJpaRepository extends JpaRepository<CustomFieldValueJpaEntity, UUID> {
    Optional<CustomFieldValueJpaEntity> findByCustomFieldDefinitionIdAndTargetId(UUID fieldId, UUID targetId);
    List<CustomFieldValueJpaEntity> findByWorkspaceIdAndObjectTypeCodeAndTargetId(UUID workspaceId, String objectType, UUID targetId);
}
