package com.company.scopery.modules.traceability.requirementsource.domain.model;
import java.util.*;
public interface RequirementSourceRepository {
    RequirementSource save(RequirementSource entity);
    Optional<RequirementSource> findByIdAndRequirementId(UUID id, UUID requirementId);
    List<RequirementSource> findByRequirementId(UUID requirementId);
}
