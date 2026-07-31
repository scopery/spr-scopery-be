package com.company.scopery.modules.traceability.usecase.domain.model;

import java.util.List;
import java.util.UUID;

public interface RequirementFunctionRepository {
    void link(UUID requirementId, UUID functionId);
    void unlink(UUID requirementId, UUID functionId);
    boolean exists(UUID requirementId, UUID functionId);
    List<UUID> findFunctionIdsByRequirementId(UUID requirementId);
    List<UUID> findRequirementIdsByFunctionId(UUID functionId);
}
