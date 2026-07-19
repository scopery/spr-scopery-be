package com.company.scopery.modules.externalparty.stakeholder.domain.model;
import java.util.*;
public interface ProjectStakeholderRepository {
    ProjectStakeholder save(ProjectStakeholder e);
    Optional<ProjectStakeholder> findByIdAndProjectId(UUID id, UUID projectId);
    List<ProjectStakeholder> findByProjectId(UUID projectId);
}
