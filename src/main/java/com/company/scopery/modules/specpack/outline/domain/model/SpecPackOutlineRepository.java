package com.company.scopery.modules.specpack.outline.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpecPackOutlineRepository {

    SpecPackOutline save(SpecPackOutline outline);

    Optional<SpecPackOutline> findById(UUID id);

    List<SpecPackOutline> findAllBySessionId(UUID sessionId);

    List<SpecPackOutline> findAllBySessionIdAndStatus(UUID sessionId, String status);

    Optional<SpecPackOutline> findLatestDraftOrApproved(UUID sessionId);
}
