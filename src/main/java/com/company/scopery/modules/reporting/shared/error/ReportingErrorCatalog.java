package com.company.scopery.modules.reporting.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum ReportingErrorCatalog implements ErrorCatalog {
    REPORT_DEFINITION_NOT_FOUND("REPORT_DEFINITION_NOT_FOUND", "Report definition not found", HttpStatus.NOT_FOUND),
    REPORT_DEFINITION_CODE_ALREADY_EXISTS("REPORT_DEFINITION_CODE_ALREADY_EXISTS", "Report definition code already exists", HttpStatus.CONFLICT),
    REPORT_DEFINITION_DEPRECATED("REPORT_DEFINITION_DEPRECATED", "Report definition is deprecated", HttpStatus.UNPROCESSABLE_ENTITY),
    REPORT_DEFINITION_INVALID_SCOPE("REPORT_DEFINITION_INVALID_SCOPE", "Report definition scope is invalid", HttpStatus.BAD_REQUEST),
    REPORT_DEFINITION_INVALID_FORMAT("REPORT_DEFINITION_INVALID_FORMAT", "Report definition format is invalid", HttpStatus.BAD_REQUEST),

    REPORT_RUN_NOT_FOUND("REPORT_RUN_NOT_FOUND", "Report run not found", HttpStatus.NOT_FOUND),
    REPORT_RUN_INVALID_FILTER("REPORT_RUN_INVALID_FILTER", "Report run filter is invalid", HttpStatus.BAD_REQUEST),
    REPORT_RUN_ACCESS_DENIED("REPORT_RUN_ACCESS_DENIED", "Report run access denied", HttpStatus.FORBIDDEN),
    REPORT_RUN_FAILED("REPORT_RUN_FAILED", "Report run failed", HttpStatus.UNPROCESSABLE_ENTITY),
    REPORT_RUN_NOT_CANCELLABLE("REPORT_RUN_NOT_CANCELLABLE", "Report run cannot be cancelled", HttpStatus.UNPROCESSABLE_ENTITY),
    REPORT_RUN_PROJECT_MISMATCH("REPORT_RUN_PROJECT_MISMATCH", "Report run project mismatch", HttpStatus.UNPROCESSABLE_ENTITY),
    REPORT_RUN_NOT_COMPLETED("REPORT_RUN_NOT_COMPLETED", "Report run is not completed", HttpStatus.UNPROCESSABLE_ENTITY),
    REPORT_PROJECT_REQUIRED("REPORT_PROJECT_REQUIRED", "Project id is required for this report", HttpStatus.BAD_REQUEST),

    REPORT_SNAPSHOT_NOT_FOUND("REPORT_SNAPSHOT_NOT_FOUND", "Report snapshot not found", HttpStatus.NOT_FOUND),
    REPORT_SNAPSHOT_IMMUTABLE("REPORT_SNAPSHOT_IMMUTABLE", "Report snapshot is immutable", HttpStatus.UNPROCESSABLE_ENTITY),
    REPORT_SNAPSHOT_ACCESS_DENIED("REPORT_SNAPSHOT_ACCESS_DENIED", "Report snapshot access denied", HttpStatus.FORBIDDEN),

    REPORT_EXPORT_NOT_FOUND("REPORT_EXPORT_NOT_FOUND", "Report export not found", HttpStatus.NOT_FOUND),
    REPORT_EXPORT_FORMAT_NOT_SUPPORTED("REPORT_EXPORT_FORMAT_NOT_SUPPORTED", "Export format is not supported", HttpStatus.BAD_REQUEST),
    REPORT_INVALID_FORMAT("REPORT_INVALID_FORMAT", "Unsupported export format", HttpStatus.BAD_REQUEST),
    REPORT_EXPORT_ACCESS_DENIED("REPORT_EXPORT_ACCESS_DENIED", "Report export access denied", HttpStatus.FORBIDDEN),
    REPORT_EXPORT_FAILED("REPORT_EXPORT_FAILED", "Report export failed", HttpStatus.UNPROCESSABLE_ENTITY),
    REPORT_EXPORT_NOT_READY("REPORT_EXPORT_NOT_READY", "Report export is not ready", HttpStatus.UNPROCESSABLE_ENTITY),
    REPORT_EXPORT_EXPIRED("REPORT_EXPORT_EXPIRED", "Report export has expired", HttpStatus.GONE),
    REPORT_EXPORT_DOWNLOAD_DENIED("REPORT_EXPORT_DOWNLOAD_DENIED", "Report export download denied", HttpStatus.FORBIDDEN),
    REPORT_EXPORT_NOT_CANCELLABLE("REPORT_EXPORT_NOT_CANCELLABLE", "Report export cannot be cancelled", HttpStatus.UNPROCESSABLE_ENTITY),

    REPORT_ACCESS_DENIED("REPORT_ACCESS_DENIED", "Report access denied", HttpStatus.FORBIDDEN),
    PROJECT_DASHBOARD_ACCESS_DENIED("PROJECT_DASHBOARD_ACCESS_DENIED", "Project dashboard access denied", HttpStatus.FORBIDDEN),
    PROJECT_DASHBOARD_SECTION_MASKED("PROJECT_DASHBOARD_SECTION_MASKED", "Dashboard section is masked", HttpStatus.FORBIDDEN),
    PROJECT_HEALTH_SOURCE_MISSING("PROJECT_HEALTH_SOURCE_MISSING", "Required health source data is missing", HttpStatus.UNPROCESSABLE_ENTITY),

    REPORT_FINANCE_ACCESS_DENIED("REPORT_FINANCE_ACCESS_DENIED", "Finance report access denied", HttpStatus.FORBIDDEN),
    REPORT_QUOTE_ACCESS_DENIED("REPORT_QUOTE_ACCESS_DENIED", "Quote report access denied", HttpStatus.FORBIDDEN),
    REPORT_RATE_ACCESS_DENIED("REPORT_RATE_ACCESS_DENIED", "Rate field access denied", HttpStatus.FORBIDDEN),
    REPORT_AI_ACCESS_DENIED("REPORT_AI_ACCESS_DENIED", "AI report field access denied", HttpStatus.FORBIDDEN),
    REPORT_CROSS_WORKSPACE_DENIED("REPORT_CROSS_WORKSPACE_DENIED", "Cross-workspace report access denied", HttpStatus.FORBIDDEN);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ReportingErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultMessage() {
        return defaultMessage;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
