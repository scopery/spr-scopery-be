package com.company.scopery.modules.projectfinance.scenario.infrastructure.mapper;

import com.company.scopery.modules.projectfinance.scenario.domain.enums.ContingencyMethod;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.FinanceScenarioStatus;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.OverheadMethod;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.RevenueSplitMethod;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.scenario.infrastructure.persistence.ProjectFinanceScenarioJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectFinanceScenarioPersistenceMapper {

    public ProjectFinanceScenario toDomain(ProjectFinanceScenarioJpaEntity e) {
        return new ProjectFinanceScenario(
                e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getEstimationRunId(),
                e.getCode(), e.getName(), e.getDescription(),
                e.getScenarioVersion() == null ? 1 : e.getScenarioVersion(),
                FinanceScenarioStatus.valueOf(e.getStatus()), e.getCurrencyCode(), e.getPlannedRevenue(),
                RevenueSplitMethod.valueOf(e.getRevenueSplitMethod()),
                e.getContingencyMethod() == null ? null : ContingencyMethod.valueOf(e.getContingencyMethod()),
                e.getContingencyPercent(), e.getContingencyFixedAmount(),
                e.getOverheadMethod() == null ? null : OverheadMethod.valueOf(e.getOverheadMethod()),
                e.getOverheadPercent(), e.getOverheadFixedAmount(), e.getTargetMarginPercent(),
                e.getAssumptionsJson(), e.getFormulaVersion(),
                Boolean.TRUE.equals(e.getCurrentFlag()), e.getApprovedAt(), e.getApprovedBy(),
                e.getArchivedAt(), e.getArchivedBy(), e.getActorUserId(), e.getTraceId(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public ProjectFinanceScenarioJpaEntity toJpaEntity(ProjectFinanceScenario d) {
        ProjectFinanceScenarioJpaEntity e = new ProjectFinanceScenarioJpaEntity();
        e.setId(d.id());
        e.setProjectId(d.projectId());
        e.setWorkspaceId(d.workspaceId());
        e.setEstimationRunId(d.estimationRunId());
        e.setCode(d.code());
        e.setName(d.name());
        e.setDescription(d.description());
        e.setScenarioVersion(d.scenarioVersion());
        e.setStatus(d.status().name());
        e.setCurrencyCode(d.currencyCode());
        e.setPlannedRevenue(d.plannedRevenue());
        e.setRevenueSplitMethod(d.revenueSplitMethod().name());
        e.setContingencyMethod(d.contingencyMethod() == null ? null : d.contingencyMethod().name());
        e.setContingencyPercent(d.contingencyPercent());
        e.setContingencyFixedAmount(d.contingencyFixedAmount());
        e.setOverheadMethod(d.overheadMethod() == null ? null : d.overheadMethod().name());
        e.setOverheadPercent(d.overheadPercent());
        e.setOverheadFixedAmount(d.overheadFixedAmount());
        e.setTargetMarginPercent(d.targetMarginPercent());
        e.setAssumptionsJson(d.assumptionsJson());
        e.setFormulaVersion(d.formulaVersion());
        e.setCurrentFlag(d.currentFlag());
        e.setApprovedAt(d.approvedAt());
        e.setApprovedBy(d.approvedBy());
        e.setArchivedAt(d.archivedAt());
        e.setArchivedBy(d.archivedBy());
        e.setActorUserId(d.actorUserId());
        e.setTraceId(d.traceId());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
