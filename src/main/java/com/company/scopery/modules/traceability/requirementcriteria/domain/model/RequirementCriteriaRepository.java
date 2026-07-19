package com.company.scopery.modules.traceability.requirementcriteria.domain.model;
import java.util.*;
public interface RequirementCriteriaRepository {
    RequirementCriteria save(RequirementCriteria entity);
    Optional<RequirementCriteria> findByIdAndRequirementId(UUID id, UUID requirementId);
    List<RequirementCriteria> findByRequirementId(UUID requirementId);
}
