package com.company.scopery.modules.projectfinance.phasefinance.infrastructure.persistence;

import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinance;
import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinanceRepository;
import com.company.scopery.modules.projectfinance.phasefinance.infrastructure.mapper.ProjectPhaseFinancePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProjectPhaseFinanceRepository implements ProjectPhaseFinanceRepository {
    private final SpringDataProjectPhaseFinanceJpaRepository springData;
    private final ProjectPhaseFinancePersistenceMapper mapper;

    public JpaProjectPhaseFinanceRepository(SpringDataProjectPhaseFinanceJpaRepository springData,
                                            ProjectPhaseFinancePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public ProjectPhaseFinance save(ProjectPhaseFinance phaseFinance) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(phaseFinance)));
    }

    @Override
    public List<ProjectPhaseFinance> saveAll(List<ProjectPhaseFinance> rows) {
        return springData.saveAllAndFlush(rows.stream().map(mapper::toJpaEntity).toList())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ProjectPhaseFinance> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<ProjectPhaseFinance> findByIdAndScenarioId(UUID id, UUID scenarioId) {
        return springData.findByIdAndFinanceScenarioId(id, scenarioId).map(mapper::toDomain);
    }

    @Override
    public List<ProjectPhaseFinance> findByScenarioId(UUID scenarioId) {
        return springData.findByFinanceScenarioIdOrderByPhaseOrderAsc(scenarioId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteByScenarioId(UUID scenarioId) {
        springData.deleteByFinanceScenarioId(scenarioId);
    }
}
