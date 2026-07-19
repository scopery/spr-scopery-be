package com.company.scopery.modules.quality.shared.error;
import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;
public enum QualityErrorCatalog implements ErrorCatalog {
    QUALITY_PLAN_NOT_FOUND("QUALITY_PLAN_NOT_FOUND", "Quality plan not found", HttpStatus.NOT_FOUND),
    QUALITY_PLAN_IMMUTABLE("QUALITY_PLAN_IMMUTABLE", "Quality plan is immutable in current status", HttpStatus.UNPROCESSABLE_ENTITY),
    TEST_PLAN_NOT_FOUND("TEST_PLAN_NOT_FOUND", "Test plan not found", HttpStatus.NOT_FOUND),
    TEST_SUITE_NOT_FOUND("TEST_SUITE_NOT_FOUND", "Test suite not found", HttpStatus.NOT_FOUND),
    TEST_CASE_NOT_FOUND("TEST_CASE_NOT_FOUND", "Test case not found", HttpStatus.NOT_FOUND),
    TEST_CASE_CODE_EXISTS("TEST_CASE_CODE_EXISTS", "Test case code already exists", HttpStatus.CONFLICT),
    TEST_CASE_IMMUTABLE("TEST_CASE_IMMUTABLE", "Approved test case is immutable", HttpStatus.UNPROCESSABLE_ENTITY),
    TEST_STEP_NOT_FOUND("TEST_STEP_NOT_FOUND", "Test step not found", HttpStatus.NOT_FOUND),
    TEST_RUN_NOT_FOUND("TEST_RUN_NOT_FOUND", "Test run not found", HttpStatus.NOT_FOUND),
    TEST_RUN_INVALID_STATUS("TEST_RUN_INVALID_STATUS", "Invalid test run status for this action", HttpStatus.UNPROCESSABLE_ENTITY),
    TEST_RESULT_NOT_FOUND("TEST_RESULT_NOT_FOUND", "Test case result not found", HttpStatus.NOT_FOUND),
    DEFECT_NOT_FOUND("DEFECT_NOT_FOUND", "Defect not found", HttpStatus.NOT_FOUND),
    DEFECT_INVALID_STATUS("DEFECT_INVALID_STATUS", "Invalid defect status for this action", HttpStatus.UNPROCESSABLE_ENTITY),
    DEFECT_RESOLUTION_REQUIRED("DEFECT_RESOLUTION_REQUIRED", "Resolution type and note are required to close", HttpStatus.BAD_REQUEST),
    DEFECT_REOPEN_REASON_REQUIRED("DEFECT_REOPEN_REASON_REQUIRED", "Reopen reason is required", HttpStatus.BAD_REQUEST),
    RELEASE_NOT_FOUND("RELEASE_NOT_FOUND", "Release package not found", HttpStatus.NOT_FOUND),
    RELEASE_CODE_EXISTS("RELEASE_CODE_EXISTS", "Release package code already exists", HttpStatus.CONFLICT),
    RELEASE_NOT_READY("RELEASE_NOT_READY", "Release readiness checks failed", HttpStatus.UNPROCESSABLE_ENTITY),
    RELEASE_INVALID_STATUS("RELEASE_INVALID_STATUS", "Invalid release status for this action", HttpStatus.UNPROCESSABLE_ENTITY),
    DEPLOYMENT_ENV_NOT_FOUND("DEPLOYMENT_ENV_NOT_FOUND", "Deployment environment not found", HttpStatus.NOT_FOUND),
    DEPLOYMENT_NOT_FOUND("DEPLOYMENT_NOT_FOUND", "Deployment record not found", HttpStatus.NOT_FOUND),
    DEPLOYMENT_INVALID_STATUS("DEPLOYMENT_INVALID_STATUS", "Invalid deployment status for this action", HttpStatus.UNPROCESSABLE_ENTITY),
    ROLLBACK_PLAN_NOT_FOUND("ROLLBACK_PLAN_NOT_FOUND", "Rollback plan not found", HttpStatus.NOT_FOUND),
    QUALITY_ACCESS_DENIED("QUALITY_ACCESS_DENIED", "Quality access denied", HttpStatus.FORBIDDEN),
    QUALITY_PROJECT_ARCHIVED("QUALITY_PROJECT_ARCHIVED", "Cannot modify quality data for archived project", HttpStatus.UNPROCESSABLE_ENTITY),
    QUALITY_NAME_REQUIRED("QUALITY_NAME_REQUIRED", "Name/title is required", HttpStatus.BAD_REQUEST);
    private final String code; private final String defaultMessage; private final HttpStatus httpStatus;
    QualityErrorCatalog(String c, String m, HttpStatus s) { code=c; defaultMessage=m; httpStatus=s; }
    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
