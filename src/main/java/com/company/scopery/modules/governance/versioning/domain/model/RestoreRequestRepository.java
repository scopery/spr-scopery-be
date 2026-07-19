package com.company.scopery.modules.governance.versioning.domain.model;
import java.util.*; import java.util.List; import java.util.UUID;
public interface RestoreRequestRepository {
    RestoreRequest save(RestoreRequest r);
    Optional<RestoreRequest> findById(UUID id);
    List<RestoreRequest> findByProjectId(UUID projectId);
}
