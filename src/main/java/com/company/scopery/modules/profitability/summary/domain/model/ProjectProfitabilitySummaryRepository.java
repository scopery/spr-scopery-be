package com.company.scopery.modules.profitability.summary.domain.model;
import java.util.Optional; import java.util.UUID;
public interface ProjectProfitabilitySummaryRepository {
    ProjectProfitabilitySummary save(ProjectProfitabilitySummary s);
    Optional<ProjectProfitabilitySummary> findByProjectIdAndCurrency(UUID projectId, String currency);
}
