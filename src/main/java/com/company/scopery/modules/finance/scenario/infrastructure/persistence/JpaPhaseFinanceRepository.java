package com.company.scopery.modules.finance.scenario.infrastructure.persistence;

import com.company.scopery.modules.finance.scenario.domain.model.PhaseFinance;
import com.company.scopery.modules.finance.scenario.domain.model.PhaseFinanceRepository;
import com.company.scopery.modules.finance.scenario.infrastructure.mapper.PhaseFinancePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaPhaseFinanceRepository implements PhaseFinanceRepository {

    private final SpringDataPhaseFinanceJpaRepository springData;
    private final PhaseFinancePersistenceMapper mapper;

    public JpaPhaseFinanceRepository(SpringDataPhaseFinanceJpaRepository springData,
                                     PhaseFinancePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public List<PhaseFinance> findAllByScenarioId(UUID scenarioId) {
        return springData.findAllByFinanceScenarioIdOrderByPhaseOrderAsc(scenarioId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public PhaseFinance save(PhaseFinance phase) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(phase)));
    }

    @Override
    public List<PhaseFinance> saveAll(List<PhaseFinance> phases) {
        List<PhaseFinanceJpaEntity> entities = phases.stream().map(mapper::toJpaEntity).toList();
        return springData.saveAllAndFlush(entities).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteAllByScenarioId(UUID scenarioId) {
        springData.deleteAllByFinanceScenarioId(scenarioId);
    }

    @Override
    public Optional<PhaseFinance> findByScenarioIdAndProjectPhaseId(UUID scenarioId, UUID projectPhaseId) {
        return springData.findByFinanceScenarioIdAndProjectPhaseId(scenarioId, projectPhaseId)
                .map(mapper::toDomain);
    }
}
