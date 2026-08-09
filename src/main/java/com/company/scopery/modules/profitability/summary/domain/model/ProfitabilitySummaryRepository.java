package com.company.scopery.modules.profitability.summary.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface ProfitabilitySummaryRepository {
    Optional<ProfitabilitySummary> findByProjectId(UUID projectId);
    ProfitabilitySummary save(ProfitabilitySummary summary);
}
