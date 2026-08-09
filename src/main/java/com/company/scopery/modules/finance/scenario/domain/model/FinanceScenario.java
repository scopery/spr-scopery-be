package com.company.scopery.modules.finance.scenario.domain.model;

import com.company.scopery.modules.finance.scenario.domain.enums.CostAdjustmentMethod;
import com.company.scopery.modules.finance.scenario.domain.enums.FinanceScenarioStatus;
import com.company.scopery.modules.finance.scenario.domain.enums.RevenueSplitMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record FinanceScenario(
        UUID id,
        UUID projectId,
        UUID workspaceId,
        UUID estimationRunId,
        String code,
        String name,
        String description,
        Integer scenarioVersion,
        FinanceScenarioStatus status,
        String currencyCode,
        BigDecimal plannedRevenue,
        RevenueSplitMethod revenueSplitMethod,
        CostAdjustmentMethod contingencyMethod,
        BigDecimal contingencyPercent,
        BigDecimal contingencyFixedAmount,
        CostAdjustmentMethod overheadMethod,
        BigDecimal overheadPercent,
        BigDecimal overheadFixedAmount,
        BigDecimal targetMarginPercent,
        String assumptionsJson,
        boolean currentFlag,
        Instant approvedAt,
        String formulaVersion,
        Integer version,
        Instant createdAt,
        Instant updatedAt
) {

    public static FinanceScenario create(
            UUID projectId,
            UUID workspaceId,
            UUID estimationRunId,
            String code,
            String name,
            String description,
            String currencyCode,
            BigDecimal plannedRevenue,
            RevenueSplitMethod revenueSplitMethod,
            CostAdjustmentMethod contingencyMethod,
            BigDecimal contingencyPercent,
            BigDecimal contingencyFixedAmount,
            CostAdjustmentMethod overheadMethod,
            BigDecimal overheadPercent,
            BigDecimal overheadFixedAmount,
            BigDecimal targetMarginPercent,
            String assumptionsJson) {
        return new FinanceScenario(
                UUID.randomUUID(),
                projectId,
                workspaceId,
                estimationRunId,
                code,
                name,
                description,
                1,
                FinanceScenarioStatus.DRAFT,
                currencyCode,
                plannedRevenue != null ? plannedRevenue : BigDecimal.ZERO,
                revenueSplitMethod,
                contingencyMethod != null ? contingencyMethod : CostAdjustmentMethod.NONE,
                contingencyPercent,
                contingencyFixedAmount,
                overheadMethod != null ? overheadMethod : CostAdjustmentMethod.NONE,
                overheadPercent,
                overheadFixedAmount,
                targetMarginPercent,
                assumptionsJson,
                false,
                null,
                "v1",
                null,
                null,
                null
        );
    }

    public FinanceScenario approve(Instant approvedAt) {
        return new FinanceScenario(
                id, projectId, workspaceId, estimationRunId, code, name, description, scenarioVersion,
                FinanceScenarioStatus.APPROVED, currencyCode, plannedRevenue, revenueSplitMethod,
                contingencyMethod, contingencyPercent, contingencyFixedAmount,
                overheadMethod, overheadPercent, overheadFixedAmount, targetMarginPercent,
                assumptionsJson, currentFlag, approvedAt, formulaVersion, version, createdAt, updatedAt);
    }

    public FinanceScenario archive() {
        return new FinanceScenario(
                id, projectId, workspaceId, estimationRunId, code, name, description, scenarioVersion,
                FinanceScenarioStatus.ARCHIVED, currencyCode, plannedRevenue, revenueSplitMethod,
                contingencyMethod, contingencyPercent, contingencyFixedAmount,
                overheadMethod, overheadPercent, overheadFixedAmount, targetMarginPercent,
                assumptionsJson, currentFlag, approvedAt, formulaVersion, version, createdAt, updatedAt);
    }

    public FinanceScenario markCurrent() {
        return new FinanceScenario(
                id, projectId, workspaceId, estimationRunId, code, name, description, scenarioVersion,
                status, currencyCode, plannedRevenue, revenueSplitMethod,
                contingencyMethod, contingencyPercent, contingencyFixedAmount,
                overheadMethod, overheadPercent, overheadFixedAmount, targetMarginPercent,
                assumptionsJson, true, approvedAt, formulaVersion, version, createdAt, updatedAt);
    }

    public FinanceScenario unmarkCurrent() {
        return new FinanceScenario(
                id, projectId, workspaceId, estimationRunId, code, name, description, scenarioVersion,
                status, currencyCode, plannedRevenue, revenueSplitMethod,
                contingencyMethod, contingencyPercent, contingencyFixedAmount,
                overheadMethod, overheadPercent, overheadFixedAmount, targetMarginPercent,
                assumptionsJson, false, approvedAt, formulaVersion, version, createdAt, updatedAt);
    }

    public boolean canApprove() {
        return status == FinanceScenarioStatus.DRAFT;
    }

    public boolean canArchive() {
        return status != FinanceScenarioStatus.ARCHIVED;
    }
}
