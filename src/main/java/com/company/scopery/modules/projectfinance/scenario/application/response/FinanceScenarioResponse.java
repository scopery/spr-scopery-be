package com.company.scopery.modules.projectfinance.scenario.application.response;

import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record FinanceScenarioResponse(
        UUID id,
        UUID projectId,
        UUID workspaceId,
        UUID estimationRunId,
        String code,
        String name,
        String description,
        int scenarioVersion,
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
        String formulaVersion,
        boolean currentFlag,
        Instant approvedAt,
        UUID approvedBy,
        Instant archivedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static FinanceScenarioResponse from(ProjectFinanceScenario s) {
        return new FinanceScenarioResponse(
                s.id(), s.projectId(), s.workspaceId(), s.estimationRunId(), s.code(), s.name(),
                s.description(), s.scenarioVersion(), s.status().name(), s.currencyCode(),
                s.plannedRevenue(), s.revenueSplitMethod().name(),
                s.contingencyMethod() == null ? null : s.contingencyMethod().name(),
                s.contingencyPercent(), s.contingencyFixedAmount(),
                s.overheadMethod() == null ? null : s.overheadMethod().name(),
                s.overheadPercent(), s.overheadFixedAmount(), s.targetMarginPercent(),
                s.assumptionsJson(), s.formulaVersion(), s.currentFlag(),
                s.approvedAt(), s.approvedBy(), s.archivedAt(), s.createdAt(), s.updatedAt());
    }
}
