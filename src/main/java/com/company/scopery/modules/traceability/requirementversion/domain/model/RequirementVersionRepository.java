package com.company.scopery.modules.traceability.requirementversion.domain.model;
import java.util.*;
public interface RequirementVersionRepository {
    RequirementVersion save(RequirementVersion entity);
    Optional<RequirementVersion> findByIdAndRequirementId(UUID id, UUID requirementId);
    List<RequirementVersion> findByRequirementId(UUID requirementId);
    int countByRequirementId(UUID requirementId);
}
