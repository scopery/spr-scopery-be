package com.company.scopery.modules.profitability.profile.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface ProfitabilityProfileRepository {
    Optional<ProfitabilityProfile> findById(UUID id);
    Optional<ProfitabilityProfile> findByProjectId(UUID projectId);
    boolean existsByProjectId(UUID projectId);
    ProfitabilityProfile save(ProfitabilityProfile profile);
}
