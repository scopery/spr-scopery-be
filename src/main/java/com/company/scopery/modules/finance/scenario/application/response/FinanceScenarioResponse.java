package com.company.scopery.modules.finance.scenario.application.response;

import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenario;

import java.math.BigDecimal;
import java.util.UUID;

public record FinanceScenarioResponse(
        UUID id,
        UUID projectId,
        UUID workspaceId,
        UUID estimationRunId,
        String code,
        String name,
        String description,
        Integer scenarioVersion,
        String status,
        String currencyCode,
        BigDecimal plannedRevenue,
        String revenueSplitMethod,
        String contingencyMethod,
        BigDecimal contingencyPercent,
        BigDecimal contingencyFixedAmount,
        String overheadMethod,
        BigDecimal overheadPercent,
        BigDecimal overheadFixedAmount,
        BigDecimal targetMarginPercent,
        String assumptionsJson,
        boolean currentFlag,
        String approvedAt,
        String formulaVersion,
        String createdAt,
        String updatedAt
) {
    public static FinanceScenarioResponse from(FinanceScenario s) {
        return new FinanceScenarioResponse(
                s.id(),
                s.projectId(),
                s.workspaceId(),
                s.estimationRunId(),
                s.code(),
                s.name(),
                s.description(),
                s.scenarioVersion(),
                s.status() != null ? s.status().name() : null,
                s.currencyCode(),
                s.plannedRevenue(),
                s.revenueSplitMethod() != null ? s.revenueSplitMethod().name() : null,
                s.contingencyMethod() != null ? s.contingencyMethod().name() : null,
                s.contingencyPercent(),
                s.contingencyFixedAmount(),
                s.overheadMethod() != null ? s.overheadMethod().name() : null,
                s.overheadPercent(),
                s.overheadFixedAmount(),
                s.targetMarginPercent(),
                s.assumptionsJson(),
                s.currentFlag(),
                s.approvedAt() != null ? s.approvedAt().toString() : null,
                s.formulaVersion(),
                s.createdAt() != null ? s.createdAt().toString() : null,
                s.updatedAt() != null ? s.updatedAt().toString() : null
        );
    }
}
