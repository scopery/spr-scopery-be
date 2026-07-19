package com.company.scopery.modules.projectbaseline.changerequestitem.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChangeRequestItemRepository {
    ChangeRequestItem save(ChangeRequestItem item);
    Optional<ChangeRequestItem> findById(UUID id);
    Optional<ChangeRequestItem> findByIdAndChangeRequestId(UUID id, UUID changeRequestId);
    List<ChangeRequestItem> findByChangeRequestId(UUID changeRequestId);
    void delete(ChangeRequestItem item);
}
