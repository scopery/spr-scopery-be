package com.company.scopery.modules.finance.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum FinanceErrorCatalog implements ErrorCatalog {

    SCENARIO_NOT_FOUND(
            "FINANCE_SCENARIO_NOT_FOUND", "Finance scenario not found", HttpStatus.NOT_FOUND),
    SCENARIO_CODE_ALREADY_EXISTS(
            "FINANCE_SCENARIO_CODE_EXISTS", "Finance scenario code already exists", HttpStatus.CONFLICT),
    SCENARIO_NOT_APPROVABLE(
            "FINANCE_SCENARIO_NOT_APPROVABLE", "Finance scenario cannot be approved in current state", HttpStatus.UNPROCESSABLE_ENTITY),
    SCENARIO_NOT_ARCHIVABLE(
            "FINANCE_SCENARIO_NOT_ARCHIVABLE", "Finance scenario cannot be archived in current state", HttpStatus.UNPROCESSABLE_ENTITY),
    COST_NOT_FOUND(
            "FINANCE_COST_NOT_FOUND", "Cost item not found", HttpStatus.NOT_FOUND),
    SUMMARY_NOT_FOUND(
            "FINANCE_SUMMARY_NOT_FOUND", "Finance summary not found", HttpStatus.NOT_FOUND),
    ESTIMATION_RUN_NOT_FOUND(
            "FINANCE_ESTIMATION_RUN_NOT_FOUND", "Estimation run not found for finance scenario", HttpStatus.NOT_FOUND);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    FinanceErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
