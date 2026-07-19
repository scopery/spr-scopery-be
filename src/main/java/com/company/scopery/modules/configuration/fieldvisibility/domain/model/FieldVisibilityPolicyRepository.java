package com.company.scopery.modules.configuration.fieldvisibility.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface FieldVisibilityPolicyRepository {
    FieldVisibilityPolicy save(FieldVisibilityPolicy policy);
    Optional<FieldVisibilityPolicy> findByFieldAndAudience(UUID fieldId, String audienceType);
    List<FieldVisibilityPolicy> findByFieldId(UUID workspaceId, UUID fieldId);
    List<FieldVisibilityPolicy> findByWorkspaceId(UUID workspaceId);
}
