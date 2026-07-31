package com.company.scopery.modules.traceability.usecase.domain.model;

import java.util.List;
import java.util.UUID;

public interface RequirementUseCaseRepository {
    void link(UUID requirementId, UUID useCaseId);
    void unlink(UUID requirementId, UUID useCaseId);
    boolean exists(UUID requirementId, UUID useCaseId);
    List<UUID> findUseCaseIdsByRequirementId(UUID requirementId);
    List<UUID> findRequirementIdsByUseCaseId(UUID useCaseId);
}
