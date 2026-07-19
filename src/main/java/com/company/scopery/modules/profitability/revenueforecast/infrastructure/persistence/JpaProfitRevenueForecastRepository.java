package com.company.scopery.modules.profitability.revenueforecast.infrastructure.persistence;

import com.company.scopery.modules.profitability.revenueforecast.domain.model.ProfitRevenueForecast;
import com.company.scopery.modules.profitability.revenueforecast.domain.model.ProfitRevenueForecastRepository;
import com.company.scopery.modules.profitability.revenueforecast.infrastructure.mapper.ProfitRevenueForecastPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProfitRevenueForecastRepository implements ProfitRevenueForecastRepository {
    private final SpringDataProfitRevenueForecastJpaRepository springData;
    private final ProfitRevenueForecastPersistenceMapper mapper;

    public JpaProfitRevenueForecastRepository(SpringDataProfitRevenueForecastJpaRepository springData,
                                               ProfitRevenueForecastPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public ProfitRevenueForecast save(ProfitRevenueForecast forecast) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(forecast)));
    }

    @Override
    public Optional<ProfitRevenueForecast> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ProfitRevenueForecast> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ProfitRevenueForecast> findByProjectIdAndStatus(UUID projectId, String status) {
        return springData.findByProjectIdAndStatusOrderByCreatedAtDesc(projectId, status).stream().map(mapper::toDomain).toList();
    }
}
