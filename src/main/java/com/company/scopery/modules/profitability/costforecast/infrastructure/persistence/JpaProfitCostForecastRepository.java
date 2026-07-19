package com.company.scopery.modules.profitability.costforecast.infrastructure.persistence;

import com.company.scopery.modules.profitability.costforecast.domain.model.ProfitCostForecast;
import com.company.scopery.modules.profitability.costforecast.domain.model.ProfitCostForecastRepository;
import com.company.scopery.modules.profitability.costforecast.infrastructure.mapper.ProfitCostForecastPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProfitCostForecastRepository implements ProfitCostForecastRepository {
    private final SpringDataProfitCostForecastJpaRepository springData;
    private final ProfitCostForecastPersistenceMapper mapper;

    public JpaProfitCostForecastRepository(SpringDataProfitCostForecastJpaRepository springData,
                                            ProfitCostForecastPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public ProfitCostForecast save(ProfitCostForecast forecast) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(forecast)));
    }

    @Override
    public Optional<ProfitCostForecast> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ProfitCostForecast> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ProfitCostForecast> findByProjectIdAndStatus(UUID projectId, String status) {
        return springData.findByProjectIdAndStatusOrderByCreatedAtDesc(projectId, status).stream().map(mapper::toDomain).toList();
    }
}
