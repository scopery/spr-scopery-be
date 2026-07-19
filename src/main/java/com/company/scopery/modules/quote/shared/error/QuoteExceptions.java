package com.company.scopery.modules.quote.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class QuoteExceptions {
    private QuoteExceptions() {}

    public static AppException quoteNotFound(UUID id) {
        return new AppException(QuoteErrorCatalog.QUOTE_NOT_FOUND,
                "Quote not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException codeAlreadyExists(String code) {
        return new AppException(QuoteErrorCatalog.QUOTE_CODE_ALREADY_EXISTS,
                "Quote code already exists: " + code, Map.of("code", code == null ? "" : code));
    }

    public static AppException projectArchived(UUID projectId) {
        return new AppException(QuoteErrorCatalog.QUOTE_PROJECT_ARCHIVED,
                "Cannot create quote for archived project: " + projectId, Map.of("projectId", projectId));
    }

    public static AppException sourceFinanceNotFound(UUID id) {
        return new AppException(QuoteErrorCatalog.QUOTE_SOURCE_FINANCE_NOT_FOUND,
                "Source finance scenario not found: " + id, Map.of("financeScenarioId", id == null ? "" : id));
    }

    public static AppException sourceFinanceProjectMismatch(UUID financeScenarioId, UUID projectId) {
        return new AppException(QuoteErrorCatalog.QUOTE_SOURCE_FINANCE_PROJECT_MISMATCH,
                "Finance scenario does not belong to project",
                Map.of("financeScenarioId", financeScenarioId, "projectId", projectId));
    }

    public static AppException sourceFinanceNotApproved(UUID id) {
        return new AppException(QuoteErrorCatalog.QUOTE_SOURCE_FINANCE_NOT_APPROVED,
                "Finance scenario must be APPROVED or current: " + id, Map.of("financeScenarioId", id));
    }

    public static AppException accessDenied() {
        return new AppException(QuoteErrorCatalog.QUOTE_ACCESS_DENIED);
    }

    public static AppException versionNotFound(UUID id) {
        return new AppException(QuoteErrorCatalog.QUOTE_VERSION_NOT_FOUND,
                "Quote version not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException versionNotDraft(UUID id) {
        return new AppException(QuoteErrorCatalog.QUOTE_VERSION_NOT_DRAFT,
                "Quote version must be DRAFT: " + id, Map.of("id", id));
    }

    public static AppException versionImmutable(UUID id) {
        return new AppException(QuoteErrorCatalog.QUOTE_VERSION_IMMUTABLE,
                "Quote version is immutable: " + id, Map.of("id", id));
    }

    public static AppException versionInvalidStatus(UUID id, String action) {
        return new AppException(QuoteErrorCatalog.QUOTE_VERSION_INVALID_STATUS,
                "Invalid status for action " + action + ": " + id,
                Map.of("id", id, "action", action == null ? "" : action));
    }

    public static AppException versionNoLines(UUID id) {
        return new AppException(QuoteErrorCatalog.QUOTE_VERSION_NO_LINES,
                "Quote version has no lines: " + id, Map.of("id", id));
    }

    public static AppException versionSummaryInvalid(String message) {
        return new AppException(QuoteErrorCatalog.QUOTE_VERSION_SUMMARY_INVALID, message);
    }

    public static AppException versionCurrentConflict(UUID quoteId) {
        return new AppException(QuoteErrorCatalog.QUOTE_VERSION_CURRENT_CONFLICT,
                "Only one current version allowed for quote: " + quoteId, Map.of("quoteId", quoteId));
    }

    public static AppException financeSnapshotInvalid(String message) {
        return new AppException(QuoteErrorCatalog.QUOTE_VERSION_FINANCE_SNAPSHOT_INVALID, message);
    }

    public static AppException lineNotFound(UUID id) {
        return new AppException(QuoteErrorCatalog.QUOTE_LINE_NOT_FOUND,
                "Quote line not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException lineInvalidQuantity() {
        return new AppException(QuoteErrorCatalog.QUOTE_LINE_INVALID_QUANTITY);
    }

    public static AppException lineInvalidUnitPrice() {
        return new AppException(QuoteErrorCatalog.QUOTE_LINE_INVALID_UNIT_PRICE);
    }

    public static AppException lineCurrencyMismatch(String expected, String actual) {
        return new AppException(QuoteErrorCatalog.QUOTE_LINE_CURRENCY_MISMATCH,
                "Currency mismatch: expected " + expected + ", got " + actual,
                Map.of("expected", expected == null ? "" : expected, "actual", actual == null ? "" : actual));
    }

    public static AppException lineSourcePhaseMismatch(UUID phaseId) {
        return new AppException(QuoteErrorCatalog.QUOTE_LINE_SOURCE_PHASE_MISMATCH,
                "Phase does not belong to project: " + phaseId, Map.of("projectPhaseId", phaseId));
    }

    public static AppException termNotFound(UUID id) {
        return new AppException(QuoteErrorCatalog.QUOTE_TERM_NOT_FOUND,
                "Quote term not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException termContentRequired() {
        return new AppException(QuoteErrorCatalog.QUOTE_TERM_CONTENT_REQUIRED);
    }

    public static AppException solverInvalidCostBase() {
        return new AppException(QuoteErrorCatalog.QUOTE_SOLVER_INVALID_COST_BASE);
    }

    public static AppException solverInvalidTargetMargin() {
        return new AppException(QuoteErrorCatalog.QUOTE_SOLVER_INVALID_TARGET_MARGIN);
    }

    public static AppException solverFailed(String message) {
        return new AppException(QuoteErrorCatalog.QUOTE_SOLVER_FAILED, message);
    }

    public static AppException discountInvalid(String message) {
        return new AppException(QuoteErrorCatalog.QUOTE_DISCOUNT_INVALID, message);
    }

    public static AppException discountReasonRequired() {
        return new AppException(QuoteErrorCatalog.QUOTE_DISCOUNT_REASON_REQUIRED);
    }

    public static AppException discountApprovalRequired() {
        return new AppException(QuoteErrorCatalog.QUOTE_DISCOUNT_APPROVAL_REQUIRED);
    }

    public static AppException submitValidationFailed(String message) {
        return new AppException(QuoteErrorCatalog.QUOTE_SUBMIT_VALIDATION_FAILED, message);
    }

    public static AppException approvalPermissionDenied() {
        return new AppException(QuoteErrorCatalog.QUOTE_APPROVAL_PERMISSION_DENIED);
    }

    public static AppException rejectionReasonRequired() {
        return new AppException(QuoteErrorCatalog.QUOTE_REJECTION_REASON_REQUIRED);
    }

    public static AppException sendNotApproved(UUID id) {
        return new AppException(QuoteErrorCatalog.QUOTE_SEND_NOT_APPROVED,
                "Quote must be APPROVED before send: " + id, Map.of("id", id));
    }

    public static AppException currencyMismatch(String expected, String actual) {
        return new AppException(QuoteErrorCatalog.QUOTE_CURRENCY_MISMATCH,
                "Currency mismatch: expected " + expected + ", got " + actual,
                Map.of("expected", expected == null ? "" : expected, "actual", actual == null ? "" : actual));
    }

    public static AppException taxNotSupported() {
        return new AppException(QuoteErrorCatalog.QUOTE_TAX_NOT_SUPPORTED);
    }

    public static AppException marginAccessDenied() {
        return new AppException(QuoteErrorCatalog.QUOTE_MARGIN_ACCESS_DENIED);
    }
}
