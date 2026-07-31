package com.company.scopery.modules.traceability.usecase.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UseCaseRepository {
    UseCase save(UseCase useCase);
    Optional<UseCase> findByIdAndProjectId(UUID id, UUID projectId);
    boolean existsByProjectIdAndKey(UUID projectId, String key);
    List<UseCase> findByProjectId(UUID projectId);
    List<UseCase> findByPrimaryFunctionId(UUID functionId);
    void deleteByIdAndProjectId(UUID id, UUID projectId);
}
