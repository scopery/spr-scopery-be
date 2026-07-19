package com.company.scopery.modules.projectfinance.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class ProjectFinanceExceptions {
    private ProjectFinanceExceptions() {}

    public static AppException scenarioNotFound(UUID id) {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_FINANCE_SCENARIO_NOT_FOUND,
                "Finance scenario not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException projectArchived(UUID projectId) {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_FINANCE_SCENARIO_PROJECT_ARCHIVED,
                "Cannot create finance scenario for archived project: " + projectId,
                Map.of("projectId", projectId));
    }

    public static AppException notDraft(UUID id) {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_FINANCE_SCENARIO_NOT_DRAFT,
                "Finance scenario must be DRAFT: " + id, Map.of("id", id));
    }

    public static AppException alreadyApproved(UUID id) {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_FINANCE_SCENARIO_ALREADY_APPROVED,
                "Finance scenario already approved: " + id, Map.of("id", id));
    }

    public static AppException estimationNotFound(UUID id) {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_FINANCE_SCENARIO_ESTIMATION_NOT_FOUND,
                "Estimation run not found: " + id, Map.of("estimationRunId", id == null ? "" : id));
    }

    public static AppException estimationNotCompleted(UUID id) {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_FINANCE_SCENARIO_ESTIMATION_NOT_COMPLETED,
                "Estimation run must be COMPLETED: " + id, Map.of("estimationRunId", id));
    }

    public static AppException estimationProjectMismatch(UUID estimationRunId, UUID projectId) {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_FINANCE_SCENARIO_ESTIMATION_PROJECT_MISMATCH,
                "Estimation run does not belong to project",
                Map.of("estimationRunId", estimationRunId, "projectId", projectId));
    }

    public static AppException currencyMismatch(String expected, String actual) {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_FINANCE_SCENARIO_CURRENCY_MISMATCH,
                "Currency mismatch: expected " + expected + ", got " + actual,
                Map.of("expected", expected == null ? "" : expected, "actual", actual == null ? "" : actual));
    }

    public static AppException validationFailed(String message) {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_FINANCE_SCENARIO_VALIDATION_FAILED, message);
    }

    public static AppException unresolvedRates(UUID scenarioId) {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_FINANCE_SCENARIO_UNRESOLVED_RATES,
                "Cannot approve scenario with unresolved estimation rates: " + scenarioId,
                Map.of("id", scenarioId));
    }

    public static AppException phaseFinanceNotFound(UUID id) {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_PHASE_FINANCE_NOT_FOUND,
                "Phase finance not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException phaseMismatch(UUID phaseId) {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_PHASE_FINANCE_PHASE_MISMATCH,
                "Phase does not belong to project: " + phaseId, Map.of("projectPhaseId", phaseId));
    }

    public static AppException customCostNotFound(UUID id) {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_CUSTOM_COST_NOT_FOUND,
                "Custom cost not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException customCostInvalidAmount() {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_CUSTOM_COST_INVALID_AMOUNT);
    }

    public static AppException customCostInvalidCategory(String category) {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_CUSTOM_COST_INVALID_CATEGORY,
                "Invalid custom cost category: " + category,
                Map.of("category", category == null ? "" : category));
    }

    public static AppException customCostPhaseMismatch(UUID phaseId) {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_CUSTOM_COST_PHASE_MISMATCH,
                "Custom cost phase mismatch: " + phaseId, Map.of("projectPhaseId", phaseId));
    }

    public static AppException customCostCurrencyMismatch() {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_CUSTOM_COST_CURRENCY_MISMATCH);
    }

    public static AppException customCostOtherRequiresDescription() {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_CUSTOM_COST_OTHER_REQUIRES_DESCRIPTION);
    }

    public static AppException vendorCostNotFound(UUID id) {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_VENDOR_COST_NOT_FOUND,
                "Vendor cost not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException vendorCostInvalidAmount() {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_VENDOR_COST_INVALID_AMOUNT);
    }

    public static AppException vendorCostPhaseMismatch(UUID phaseId) {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_VENDOR_COST_PHASE_MISMATCH,
                "Vendor cost phase mismatch: " + phaseId, Map.of("projectPhaseId", phaseId));
    }

    public static AppException vendorCostCurrencyMismatch() {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_VENDOR_COST_CURRENCY_MISMATCH);
    }

    public static AppException revenuePercentInvalid() {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_REVENUE_SPLIT_INVALID_PERCENT_TOTAL);
    }

    public static AppException revenueAmountInvalid() {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_REVENUE_SPLIT_INVALID_AMOUNT_TOTAL);
    }

    public static AppException revenueDirectCostZero() {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_REVENUE_SPLIT_DIRECT_COST_ZERO);
    }

    public static AppException invalidRevenueSplitMethod(String method) {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_REVENUE_SPLIT_INVALID_METHOD,
                "Invalid revenue split method: " + method,
                Map.of("revenueSplitMethod", method == null ? "" : method));
    }

    public static AppException invalidOverhead() {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_OVERHEAD_POLICY_INVALID);
    }

    public static AppException invalidContingency() {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_CONTINGENCY_INVALID);
    }

    public static AppException accessDenied() {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_FINANCE_ACCESS_DENIED);
    }

    public static AppException marginAccessDenied() {
        return new AppException(ProjectFinanceErrorCatalog.PROJECT_FINANCE_MARGIN_ACCESS_DENIED);
    }
}
