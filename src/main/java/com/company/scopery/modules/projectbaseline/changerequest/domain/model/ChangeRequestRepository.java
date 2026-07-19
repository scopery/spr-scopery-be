package com.company.scopery.modules.projectbaseline.changerequest.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChangeRequestRepository {
    ChangeRequest save(ChangeRequest changeRequest);
    Optional<ChangeRequest> findById(UUID id);
    Optional<ChangeRequest> findByIdAndProjectId(UUID id, UUID projectId);
    List<ChangeRequest> findByProjectId(UUID projectId);
    boolean existsByProjectIdAndCode(UUID projectId, String code);
}
