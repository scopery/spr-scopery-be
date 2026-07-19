package com.company.scopery.modules.projectbaseline.changeorder.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChangeOrderRepository {
    ChangeOrder save(ChangeOrder changeOrder);
    Optional<ChangeOrder> findById(UUID id);
    Optional<ChangeOrder> findByIdAndProjectId(UUID id, UUID projectId);
    List<ChangeOrder> findByChangeRequestId(UUID changeRequestId);
    boolean existsByProjectIdAndCode(UUID projectId, String code);
}
