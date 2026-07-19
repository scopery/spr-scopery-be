package com.company.scopery.modules.trust.retention.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface RetentionPolicyRepository {
    RetentionPolicy save(RetentionPolicy p);
    Optional<RetentionPolicy> findById(UUID id);
    List<RetentionPolicy> findByWorkspaceId(UUID workspaceId);
}
