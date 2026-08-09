package com.company.scopery.modules.finance.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class FinanceExceptions {
    private FinanceExceptions() {}

    public static AppException scenarioNotFound(UUID id) {
        return new AppException(FinanceErrorCatalog.SCENARIO_NOT_FOUND,
                "Finance scenario not found: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException scenarioCodeExists(String code) {
        return new AppException(FinanceErrorCatalog.SCENARIO_CODE_ALREADY_EXISTS,
                "Finance scenario code already exists: " + code,
                Map.of("code", code == null ? "" : code));
    }

    public static AppException scenarioNotApprovable(UUID id) {
        return new AppException(FinanceErrorCatalog.SCENARIO_NOT_APPROVABLE,
                "Finance scenario cannot be approved in current state: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException scenarioNotArchivable(UUID id) {
        return new AppException(FinanceErrorCatalog.SCENARIO_NOT_ARCHIVABLE,
                "Finance scenario cannot be archived in current state: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException costNotFound(UUID id) {
        return new AppException(FinanceErrorCatalog.COST_NOT_FOUND,
                "Cost item not found: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException summaryNotFound(UUID scenarioId) {
        return new AppException(FinanceErrorCatalog.SUMMARY_NOT_FOUND,
                "Finance summary not found for scenario: " + scenarioId,
                Map.of("scenarioId", scenarioId == null ? "" : scenarioId.toString()));
    }

    public static AppException estimationRunNotFound(UUID runId) {
        return new AppException(FinanceErrorCatalog.ESTIMATION_RUN_NOT_FOUND,
                "Estimation run not found for finance scenario: " + runId,
                Map.of("runId", runId == null ? "" : runId.toString()));
    }
}
