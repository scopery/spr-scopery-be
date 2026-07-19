package com.company.scopery.modules.servicesupport.queue.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupportQueueRepository {
    SupportQueue save(SupportQueue queue);
    Optional<SupportQueue> findById(UUID id);
    List<SupportQueue> findByWorkspaceId(UUID workspaceId);
}
