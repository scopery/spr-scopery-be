package com.company.scopery.modules.configuration.validation.domain.model;
import java.util.*; import java.util.UUID;
public interface CustomFieldValidationRuleRepository {
    CustomFieldValidationRule save(CustomFieldValidationRule r);
    Optional<CustomFieldValidationRule> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<CustomFieldValidationRule> findByFieldId(UUID fieldId);
    void delete(UUID id);
}
