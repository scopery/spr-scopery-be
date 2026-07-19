package com.company.scopery.modules.trust.retention.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface RetentionJobRepository {
    RetentionJob save(RetentionJob j);
    Optional<RetentionJob> findById(UUID id);
    List<RetentionJob> findByWorkspaceId(UUID workspaceId);
}
