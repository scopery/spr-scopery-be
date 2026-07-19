package com.company.scopery.modules.configuration.tag.domain.model;
import java.util.*; import java.util.UUID;
public interface TagAssignmentRepository {
    TagAssignment save(TagAssignment a);
    Optional<TagAssignment> findById(UUID id);
    List<TagAssignment> findActiveByWorkspace(UUID workspaceId);
}
