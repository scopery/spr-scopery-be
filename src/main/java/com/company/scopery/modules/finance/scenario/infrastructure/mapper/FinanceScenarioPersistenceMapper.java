package com.company.scopery.modules.finance.scenario.infrastructure.mapper;

import com.company.scopery.modules.finance.scenario.domain.enums.CostAdjustmentMethod;
import com.company.scopery.modules.finance.scenario.domain.enums.FinanceScenarioStatus;
import com.company.scopery.modules.finance.scenario.domain.enums.RevenueSplitMethod;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenario;
import com.company.scopery.modules.finance.scenario.infrastructure.persistence.FinanceScenarioJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class FinanceScenarioPersistenceMapper {

    public FinanceScenario toDomain(FinanceScenarioJpaEntity e) {
        return new FinanceScenario(
                e.getId(),
                e.getProjectId(),
                e.getWorkspaceId(),
                e.getEstimationRunId(),
                e.getCode(),
                e.getName(),
                e.getDescription(),
                e.getScenarioVersion(),
                FinanceScenarioStatus.valueOf(e.getStatus()),
                e.getCurrencyCode(),
                e.getPlannedRevenue(),
                e.getRevenueSplitMethod() != null ? RevenueSplitMethod.valueOf(e.getRevenueSplitMethod()) : null,
                e.getContingencyMethod() != null ? CostAdjustmentMethod.valueOf(e.getContingencyMethod()) : CostAdjustmentMethod.NONE,
                e.getContingencyPercent(),
                e.getContingencyFixedAmount(),
                e.getOverheadMethod() != null ? CostAdjustmentMethod.valueOf(e.getOverheadMethod()) : CostAdjustmentMethod.NONE,
                e.getOverheadPercent(),
                e.getOverheadFixedAmount(),
                e.getTargetMarginPercent(),
                e.getAssumptionsJson(),
                e.isCurrentFlag(),
                e.getApprovedAt(),
                e.getFormulaVersion(),
                e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public FinanceScenarioJpaEntity toJpaEntity(FinanceScenario d) {
        FinanceScenarioJpaEntity e = new FinanceScenarioJpaEntity();
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
        e.setRevenueSplitMethod(d.revenueSplitMethod() != null ? d.revenueSplitMethod().name() : null);
        e.setContingencyMethod(d.contingencyMethod() != null ? d.contingencyMethod().name() : CostAdjustmentMethod.NONE.name());
        e.setContingencyPercent(d.contingencyPercent());
        e.setContingencyFixedAmount(d.contingencyFixedAmount());
        e.setOverheadMethod(d.overheadMethod() != null ? d.overheadMethod().name() : CostAdjustmentMethod.NONE.name());
        e.setOverheadPercent(d.overheadPercent());
        e.setOverheadFixedAmount(d.overheadFixedAmount());
        e.setTargetMarginPercent(d.targetMarginPercent());
        e.setAssumptionsJson(d.assumptionsJson());
        e.setCurrentFlag(d.currentFlag());
        e.setApprovedAt(d.approvedAt());
        e.setFormulaVersion(d.formulaVersion());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
