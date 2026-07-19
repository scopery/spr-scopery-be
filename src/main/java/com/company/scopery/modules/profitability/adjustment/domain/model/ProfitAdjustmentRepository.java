package com.company.scopery.modules.profitability.adjustment.domain.model;
import java.util.*; import java.util.UUID;
public interface ProfitAdjustmentRepository {
    ProfitAdjustment save(ProfitAdjustment a);
    List<ProfitAdjustment> findByProjectId(UUID projectId);
    Optional<ProfitAdjustment> findByIdAndProjectId(UUID id, UUID projectId);
}
