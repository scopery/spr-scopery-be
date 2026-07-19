package com.company.scopery.modules.configuration.fieldvisibility.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataFieldVisibilityPolicyJpaRepository extends JpaRepository<FieldVisibilityPolicyJpaEntity, UUID> {
    Optional<FieldVisibilityPolicyJpaEntity> findByCustomFieldDefinitionIdAndAudienceType(UUID fieldId, String audienceType);
    List<FieldVisibilityPolicyJpaEntity> findByWorkspaceIdAndCustomFieldDefinitionId(UUID workspaceId, UUID fieldId);
    List<FieldVisibilityPolicyJpaEntity> findByWorkspaceId(UUID workspaceId);
}
