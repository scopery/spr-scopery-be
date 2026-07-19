package com.company.scopery.modules.externalparty.relationship.domain.model;
import java.util.*;
public interface ProjectExternalPartyRelationshipRepository {
    ProjectExternalPartyRelationship save(ProjectExternalPartyRelationship entity);
    Optional<ProjectExternalPartyRelationship> findByIdAndProjectId(UUID id, UUID projectId);
    List<ProjectExternalPartyRelationship> findByProjectId(UUID projectId);
}
