package com.company.scopery.modules.configuration.fieldvalue.domain.model;
import java.util.*; import java.util.UUID;
public interface CustomFieldValueRepository {
    CustomFieldValue save(CustomFieldValue v);
    Optional<CustomFieldValue> findByFieldAndTarget(UUID fieldId, UUID targetId);
    List<CustomFieldValue> findByWorkspaceObjectTarget(UUID workspaceId, String objectType, UUID targetId);
}
