package com.company.scopery.modules.profitability.ratecard.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfitRateCardRepository {
    ProfitRateCard save(ProfitRateCard rateCard);
    Optional<ProfitRateCard> findById(UUID id);
    List<ProfitRateCard> findByWorkspaceId(UUID workspaceId);
    List<ProfitRateCard> findByProjectId(UUID projectId);
    boolean existsByWorkspaceIdAndProjectIdAndRateCode(UUID workspaceId, UUID projectId, String rateCode);
}
