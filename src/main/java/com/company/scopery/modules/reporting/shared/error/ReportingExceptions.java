package com.company.scopery.modules.reporting.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class ReportingExceptions {
    private ReportingExceptions() {}

    public static AppException definitionNotFound(String code) {
        return new AppException(ReportingErrorCatalog.REPORT_DEFINITION_NOT_FOUND,
                "Report definition not found: " + code, Map.of("code", code == null ? "" : code));
    }

    public static AppException definitionDeprecated(String code) {
        return new AppException(ReportingErrorCatalog.REPORT_DEFINITION_DEPRECATED,
                "Report definition is deprecated: " + code, Map.of("code", code == null ? "" : code));
    }

    public static AppException runNotFound(UUID id) {
        return new AppException(ReportingErrorCatalog.REPORT_RUN_NOT_FOUND,
                "Report run not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException runNotCancellable(UUID id) {
        return new AppException(ReportingErrorCatalog.REPORT_RUN_NOT_CANCELLABLE,
                "Report run cannot be cancelled: " + id, Map.of("id", id));
    }

    public static AppException snapshotNotFound(UUID reportRunId) {
        return new AppException(ReportingErrorCatalog.REPORT_SNAPSHOT_NOT_FOUND,
                "Report snapshot not found for run: " + reportRunId, Map.of("reportRunId", reportRunId));
    }

    public static AppException exportNotFound(UUID id) {
        return new AppException(ReportingErrorCatalog.REPORT_EXPORT_NOT_FOUND,
                "Report export not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException exportExpired(UUID id) {
        return new AppException(ReportingErrorCatalog.REPORT_EXPORT_EXPIRED,
                "Report export expired: " + id, Map.of("id", id));
    }

    public static AppException exportDownloadDenied(UUID id) {
        return new AppException(ReportingErrorCatalog.REPORT_EXPORT_DOWNLOAD_DENIED,
                "Report export download denied: " + id, Map.of("id", id));
    }

    public static AppException exportNotCancellable(UUID id) {
        return new AppException(ReportingErrorCatalog.REPORT_EXPORT_NOT_CANCELLABLE,
                "Report export cannot be cancelled: " + id, Map.of("id", id));
    }

    public static AppException exportNotReady(UUID id) {
        return new AppException(ReportingErrorCatalog.REPORT_EXPORT_NOT_READY,
                "Report export is not ready: " + id, Map.of("id", id));
    }

    public static AppException accessDenied() {
        return new AppException(ReportingErrorCatalog.REPORT_ACCESS_DENIED);
    }

    public static AppException financeAccessDenied() {
        return new AppException(ReportingErrorCatalog.REPORT_FINANCE_ACCESS_DENIED);
    }

    public static AppException quoteAccessDenied() {
        return new AppException(ReportingErrorCatalog.REPORT_QUOTE_ACCESS_DENIED);
    }

    public static AppException invalidFormat(String format) {
        return new AppException(ReportingErrorCatalog.REPORT_EXPORT_FORMAT_NOT_SUPPORTED,
                "Unsupported format: " + format, Map.of("format", format == null ? "" : format));
    }

    public static AppException runNotCompleted(UUID id) {
        return new AppException(ReportingErrorCatalog.REPORT_RUN_NOT_COMPLETED,
                "Report run not completed: " + id, Map.of("id", id));
    }

    public static AppException projectRequired() {
        return new AppException(ReportingErrorCatalog.REPORT_PROJECT_REQUIRED);
    }

    public static AppException healthSourceMissing(UUID projectId) {
        return new AppException(ReportingErrorCatalog.PROJECT_HEALTH_SOURCE_MISSING,
                "Required health source data is missing for project: " + projectId,
                Map.of("projectId", projectId));
    }
}
