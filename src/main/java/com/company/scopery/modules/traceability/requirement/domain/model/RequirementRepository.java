package com.company.scopery.modules.traceability.requirement.domain.model;
import java.util.*;
public interface RequirementRepository {
    Requirement save(Requirement e);
    Optional<Requirement> findByIdAndProjectId(UUID id, UUID projectId);
    List<Requirement> findByProjectId(UUID projectId);
}
