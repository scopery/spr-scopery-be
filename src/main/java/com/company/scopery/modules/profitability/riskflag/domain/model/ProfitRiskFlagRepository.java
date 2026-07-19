package com.company.scopery.modules.profitability.riskflag.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfitRiskFlagRepository {
    ProfitRiskFlag save(ProfitRiskFlag riskFlag);
    Optional<ProfitRiskFlag> findById(UUID id);
    List<ProfitRiskFlag> findByProjectId(UUID projectId);
    List<ProfitRiskFlag> findByProjectIdAndStatus(UUID projectId, String status);
}
