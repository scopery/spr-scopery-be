package com.company.scopery.modules.configuration.validation.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataCustomFieldValidationRuleJpaRepository extends JpaRepository<CustomFieldValidationRuleJpaEntity, UUID> {
    Optional<CustomFieldValidationRuleJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<CustomFieldValidationRuleJpaEntity> findByCustomFieldDefinitionId(UUID fieldId);
}
