package com.company.scopery.modules.estimation.wbsrollup.infrastructure.persistence;

import com.company.scopery.modules.estimation.wbsrollup.domain.model.WbsEstimateRollup;
import com.company.scopery.modules.estimation.wbsrollup.domain.model.WbsEstimateRollupRepository;
import com.company.scopery.modules.estimation.wbsrollup.infrastructure.mapper.WbsEstimateRollupPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.UUID;

@Repository
public class JpaWbsEstimateRollupRepository implements WbsEstimateRollupRepository {
    private final SpringDataWbsEstimateRollupJpaRepository springData;
    private final WbsEstimateRollupPersistenceMapper mapper;
    public JpaWbsEstimateRollupRepository(SpringDataWbsEstimateRollupJpaRepository springData,
                                          WbsEstimateRollupPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public WbsEstimateRollup save(WbsEstimateRollup rollup) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(rollup)));
    }
    @Override public List<WbsEstimateRollup> saveAll(List<WbsEstimateRollup> rollups) {
        return rollups.stream().map(this::save).toList();
    }
    @Override public List<WbsEstimateRollup> findAllByEstimationRunId(UUID estimationRunId) {
        return springData.findAllByEstimationRunIdOrderByDepthAsc(estimationRunId).stream().map(mapper::toDomain).toList();
    }
}
