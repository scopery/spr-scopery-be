package com.company.scopery.modules.profitability.costforecast.infrastructure.mapper;

import com.company.scopery.modules.profitability.costforecast.domain.model.ProfitCostForecast;
import com.company.scopery.modules.profitability.costforecast.infrastructure.persistence.ProfitCostForecastJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProfitCostForecastPersistenceMapper {

    public ProfitCostForecast toDomain(ProfitCostForecastJpaEntity e) {
        return new ProfitCostForecast(
                e.getId(), e.getWorkspaceId(), e.getProjectId(),
                e.getProfitabilityProfileId(), e.getForecastType(), e.getCurrency(),
                e.getForecastAmount(), e.getConfidencePercent(), e.getForecastDate(),
                e.getAssumptionNotes(), e.getGeneratedBy(), e.getStatus(),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public ProfitCostForecastJpaEntity toJpa(ProfitCostForecast d) {
        ProfitCostForecastJpaEntity e = new ProfitCostForecastJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setProjectId(d.projectId());
        e.setProfitabilityProfileId(d.profitabilityProfileId());
        e.setForecastType(d.forecastType());
        e.setCurrency(d.currency());
        e.setForecastAmount(d.forecastAmount());
        e.setConfidencePercent(d.confidencePercent());
        e.setForecastDate(d.forecastDate());
        e.setAssumptionNotes(d.assumptionNotes());
        e.setGeneratedBy(d.generatedBy());
        e.setStatus(d.status());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
