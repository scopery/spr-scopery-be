package com.company.scopery.modules.estimation.wbsrollup.domain.model;

import java.util.List;
import java.util.UUID;

public interface WbsEstimateRollupRepository {
    WbsEstimateRollup save(WbsEstimateRollup rollup);
    List<WbsEstimateRollup> saveAll(List<WbsEstimateRollup> rollups);
    List<WbsEstimateRollup> findAllByEstimationRunId(UUID estimationRunId);
}
