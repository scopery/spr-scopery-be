package com.company.scopery.modules.estimation.estimationrun.infrastructure.mapper;

import com.company.scopery.modules.estimation.estimationrun.domain.enums.CalculationMode;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.CurrencyPolicy;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.EstimationRunStatus;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.RateTargetDateStrategy;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRun;
import com.company.scopery.modules.estimation.estimationrun.infrastructure.persistence.EstimationRunJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class EstimationRunPersistenceMapper {

    public EstimationRun toDomain(EstimationRunJpaEntity e) {
        return new EstimationRun(
                e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getScheduleRunId(),
                e.getName(), e.getDescription(),
                EstimationRunStatus.valueOf(e.getStatus()),
                CalculationMode.valueOf(e.getCalculationMode()),
                RateTargetDateStrategy.valueOf(e.getRateTargetDateStrategy()),
                CurrencyPolicy.valueOf(e.getCurrencyPolicy()),
                e.getAssumptionsJson(), e.getResultSummaryJson(), e.getErrorCode(), e.getErrorMessage(),
                e.getStartedAt(), e.getCompletedAt(), e.getActorUserId(), e.getTraceId(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public EstimationRunJpaEntity toJpaEntity(EstimationRun d) {
        EstimationRunJpaEntity e = new EstimationRunJpaEntity();
        e.setId(d.id());
        e.setProjectId(d.projectId());
        e.setWorkspaceId(d.workspaceId());
        e.setScheduleRunId(d.scheduleRunId());
        e.setName(d.name());
        e.setDescription(d.description());
        e.setStatus(d.status().name());
        e.setCalculationMode(d.calculationMode().name());
        e.setRateTargetDateStrategy(d.rateTargetDateStrategy().name());
        e.setCurrencyPolicy(d.currencyPolicy().name());
        e.setAssumptionsJson(d.assumptionsJson());
        e.setResultSummaryJson(d.resultSummaryJson());
        e.setErrorCode(d.errorCode());
        e.setErrorMessage(d.errorMessage());
        e.setStartedAt(d.startedAt());
        e.setCompletedAt(d.completedAt());
        e.setActorUserId(d.actorUserId());
        e.setTraceId(d.traceId());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
