package com.company.scopery.modules.estimation.phaserollup.domain.model;

import java.util.List;
import java.util.UUID;

public interface PhaseEstimateRollupRepository {
    PhaseEstimateRollup save(PhaseEstimateRollup rollup);
    List<PhaseEstimateRollup> saveAll(List<PhaseEstimateRollup> rollups);
    List<PhaseEstimateRollup> findAllByEstimationRunId(UUID estimationRunId);
}
