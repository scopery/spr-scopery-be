package com.company.scopery.modules.estimation.phaserollup.infrastructure.persistence;

import com.company.scopery.modules.estimation.phaserollup.domain.model.PhaseEstimateRollup;
import com.company.scopery.modules.estimation.phaserollup.domain.model.PhaseEstimateRollupRepository;
import com.company.scopery.modules.estimation.phaserollup.infrastructure.mapper.PhaseEstimateRollupPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.UUID;

@Repository
public class JpaPhaseEstimateRollupRepository implements PhaseEstimateRollupRepository {
    private final SpringDataPhaseEstimateRollupJpaRepository springData;
    private final PhaseEstimateRollupPersistenceMapper mapper;
    public JpaPhaseEstimateRollupRepository(SpringDataPhaseEstimateRollupJpaRepository springData,
                                            PhaseEstimateRollupPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public PhaseEstimateRollup save(PhaseEstimateRollup rollup) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(rollup)));
    }
    @Override public List<PhaseEstimateRollup> saveAll(List<PhaseEstimateRollup> rollups) {
        return rollups.stream().map(this::save).toList();
    }
    @Override public List<PhaseEstimateRollup> findAllByEstimationRunId(UUID estimationRunId) {
        return springData.findAllByEstimationRunId(estimationRunId).stream().map(mapper::toDomain).toList();
    }
}
