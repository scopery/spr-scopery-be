package com.company.scopery.modules.traceability.usecase.domain.model;

import java.util.List;
import java.util.UUID;

public interface UseCaseSupportingFunctionRepository {
    void link(UUID useCaseId, UUID functionId);
    void unlink(UUID useCaseId, UUID functionId);
    boolean exists(UUID useCaseId, UUID functionId);
    List<UUID> findFunctionIdsByUseCaseId(UUID useCaseId);
    List<UUID> findUseCaseIdsByFunctionId(UUID functionId);
}
