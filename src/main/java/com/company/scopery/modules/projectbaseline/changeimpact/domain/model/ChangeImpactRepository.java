package com.company.scopery.modules.projectbaseline.changeimpact.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface ChangeImpactRepository {
    ChangeImpact save(ChangeImpact impact);
    Optional<ChangeImpact> findByChangeRequestId(UUID changeRequestId);
}
