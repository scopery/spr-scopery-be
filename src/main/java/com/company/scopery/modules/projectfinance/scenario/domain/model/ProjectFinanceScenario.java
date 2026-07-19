package com.company.scopery.modules.projectfinance.scenario.domain.model;

import com.company.scopery.modules.projectfinance.scenario.domain.enums.ContingencyMethod;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.FinanceScenarioStatus;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.OverheadMethod;
import com.company.scopery.modules.projectfinance.scenario.domain.enums.RevenueSplitMethod;
import com.company.scopery.modules.projectfinance.shared.constant.ProjectFinanceFormulaVersions;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProjectFinanceScenario(
        UUID id,
        UUID projectId,
        UUID workspaceId,
        UUID estimationRunId,
        String code,
        String name,
        String description,
        int scenarioVersion,
        FinanceScenarioStatus status,
        String currencyCode,
        BigDecimal plannedRevenue,
        RevenueSplitMethod revenueSplitMethod,
        ContingencyMethod contingencyMethod,
        BigDecimal contingencyPercent,
        BigDecimal contingencyFixedAmount,
        OverheadMethod overheadMethod,
        BigDecimal overheadPercent,
        BigDecimal overheadFixedAmount,
        BigDecimal targetMarginPercent,
        String assumptionsJson,
        String formulaVersion,
        boolean currentFlag,
        Instant approvedAt,
        UUID approvedBy,
        Instant archivedAt,
        UUID archivedBy,
        UUID actorUserId,
        String traceId,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectFinanceScenario create(
            UUID projectId,
            UUID workspaceId,
            UUID estimationRunId,
            String code,
            String name,
            String description,
            String currencyCode,
            BigDecimal plannedRevenue,
            RevenueSplitMethod revenueSplitMethod,
            ContingencyMethod contingencyMethod,
            BigDecimal contingencyPercent,
            BigDecimal contingencyFixedAmount,
            OverheadMethod overheadMethod,
            BigDecimal overheadPercent,
            BigDecimal overheadFixedAmount,
            BigDecimal targetMarginPercent,
            String assumptionsJson,
            UUID actorUserId,
            String traceId) {
        return new ProjectFinanceScenario(
                UUID.randomUUID(), projectId, workspaceId, estimationRunId, code, name, description,
                1, FinanceScenarioStatus.DRAFT, currencyCode,
                plannedRevenue == null ? BigDecimal.ZERO : plannedRevenue,
                revenueSplitMethod, contingencyMethod, contingencyPercent, contingencyFixedAmount,
                overheadMethod, overheadPercent, overheadFixedAmount, targetMarginPercent,
                assumptionsJson, ProjectFinanceFormulaVersions.FINANCE_V1,
                false, null, null, null, null, actorUserId, traceId, 0, null, null);
    }

    public boolean isDraft() {
        return status == FinanceScenarioStatus.DRAFT;
    }

    public boolean isEditable() {
        return status == FinanceScenarioStatus.DRAFT;
    }

    public ProjectFinanceScenario updateDraft(
            String name,
            String description,
            BigDecimal plannedRevenue,
            RevenueSplitMethod revenueSplitMethod,
            ContingencyMethod contingencyMethod,
            BigDecimal contingencyPercent,
            BigDecimal contingencyFixedAmount,
            OverheadMethod overheadMethod,
            BigDecimal overheadPercent,
            BigDecimal overheadFixedAmount,
            BigDecimal targetMarginPercent,
            String assumptionsJson) {
        return new ProjectFinanceScenario(
                id, projectId, workspaceId, estimationRunId, code, name, description,
                scenarioVersion, status, currencyCode, plannedRevenue, revenueSplitMethod,
                contingencyMethod, contingencyPercent, contingencyFixedAmount,
                overheadMethod, overheadPercent, overheadFixedAmount, targetMarginPercent,
                assumptionsJson, formulaVersion, currentFlag, approvedAt, approvedBy,
                archivedAt, archivedBy, actorUserId, traceId, version, createdAt, updatedAt);
    }

    public ProjectFinanceScenario approve(UUID actorId) {
        return new ProjectFinanceScenario(
                id, projectId, workspaceId, estimationRunId, code, name, description,
                scenarioVersion, FinanceScenarioStatus.APPROVED, currencyCode, plannedRevenue,
                revenueSplitMethod, contingencyMethod, contingencyPercent, contingencyFixedAmount,
                overheadMethod, overheadPercent, overheadFixedAmount, targetMarginPercent,
                assumptionsJson, formulaVersion, currentFlag, Instant.now(), actorId,
                archivedAt, archivedBy, actorUserId, traceId, version, createdAt, updatedAt);
    }

    public ProjectFinanceScenario withCurrentFlag(boolean current) {
        return new ProjectFinanceScenario(
                id, projectId, workspaceId, estimationRunId, code, name, description,
                scenarioVersion, status, currencyCode, plannedRevenue, revenueSplitMethod,
                contingencyMethod, contingencyPercent, contingencyFixedAmount,
                overheadMethod, overheadPercent, overheadFixedAmount, targetMarginPercent,
                assumptionsJson, formulaVersion, current, approvedAt, approvedBy,
                archivedAt, archivedBy, actorUserId, traceId, version, createdAt, updatedAt);
    }

    public ProjectFinanceScenario archive(UUID actorId) {
        return new ProjectFinanceScenario(
                id, projectId, workspaceId, estimationRunId, code, name, description,
                scenarioVersion, FinanceScenarioStatus.ARCHIVED, currencyCode, plannedRevenue,
                revenueSplitMethod, contingencyMethod, contingencyPercent, contingencyFixedAmount,
                overheadMethod, overheadPercent, overheadFixedAmount, targetMarginPercent,
                assumptionsJson, formulaVersion, false, approvedAt, approvedBy,
                Instant.now(), actorId, actorUserId, traceId, version, createdAt, updatedAt);
    }

    public ProjectFinanceScenario asDraftDuplicate(String newName, UUID actorId, String newTraceId) {
        return new ProjectFinanceScenario(
                UUID.randomUUID(), projectId, workspaceId, estimationRunId, null, newName, description,
                scenarioVersion + 1, FinanceScenarioStatus.DRAFT, currencyCode, plannedRevenue,
                revenueSplitMethod, contingencyMethod, contingencyPercent, contingencyFixedAmount,
                overheadMethod, overheadPercent, overheadFixedAmount, targetMarginPercent,
                assumptionsJson, ProjectFinanceFormulaVersions.FINANCE_V1, false,
                null, null, null, null, actorId, newTraceId, 0, null, null);
    }
}
