package com.company.scopery.modules.resourcecapacity.conflict.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface AssignmentConflictRepository {
    AssignmentConflict save(AssignmentConflict c);
    Optional<AssignmentConflict> findById(UUID id);
    List<AssignmentConflict> findByProjectId(UUID projectId);
}
