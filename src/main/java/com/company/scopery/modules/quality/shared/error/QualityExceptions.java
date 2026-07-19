package com.company.scopery.modules.quality.shared.error;
import com.company.scopery.common.exception.AppException;
import java.util.Map; import java.util.UUID;
public final class QualityExceptions {
    private QualityExceptions() {}
    public static AppException qualityPlanNotFound(UUID id) {
        return new AppException(QualityErrorCatalog.QUALITY_PLAN_NOT_FOUND, "Quality plan not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException qualityPlanImmutable(UUID id) {
        return new AppException(QualityErrorCatalog.QUALITY_PLAN_IMMUTABLE, "Quality plan immutable: " + id, Map.of("id", id));
    }
    public static AppException testPlanNotFound(UUID id) {
        return new AppException(QualityErrorCatalog.TEST_PLAN_NOT_FOUND, "Test plan not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException testSuiteNotFound(UUID id) {
        return new AppException(QualityErrorCatalog.TEST_SUITE_NOT_FOUND, "Test suite not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException testCaseNotFound(UUID id) {
        return new AppException(QualityErrorCatalog.TEST_CASE_NOT_FOUND, "Test case not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException testCaseCodeExists(String code) {
        return new AppException(QualityErrorCatalog.TEST_CASE_CODE_EXISTS, "Code exists: " + code, Map.of("code", code == null ? "" : code));
    }
    public static AppException testCaseImmutable(UUID id) {
        return new AppException(QualityErrorCatalog.TEST_CASE_IMMUTABLE, "Test case immutable: " + id, Map.of("id", id));
    }
    public static AppException testStepNotFound(UUID id) {
        return new AppException(QualityErrorCatalog.TEST_STEP_NOT_FOUND, "Test step not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException testRunNotFound(UUID id) {
        return new AppException(QualityErrorCatalog.TEST_RUN_NOT_FOUND, "Test run not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException testRunInvalidStatus(String d) {
        return new AppException(QualityErrorCatalog.TEST_RUN_INVALID_STATUS, d, Map.of());
    }
    public static AppException testResultNotFound(UUID id) {
        return new AppException(QualityErrorCatalog.TEST_RESULT_NOT_FOUND, "Test result not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException defectNotFound(UUID id) {
        return new AppException(QualityErrorCatalog.DEFECT_NOT_FOUND, "Defect not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException defectInvalidStatus(String d) {
        return new AppException(QualityErrorCatalog.DEFECT_INVALID_STATUS, d, Map.of());
    }
    public static AppException defectResolutionRequired() {
        return new AppException(QualityErrorCatalog.DEFECT_RESOLUTION_REQUIRED);
    }
    public static AppException defectReopenReasonRequired() {
        return new AppException(QualityErrorCatalog.DEFECT_REOPEN_REASON_REQUIRED);
    }
    public static AppException releaseNotFound(UUID id) {
        return new AppException(QualityErrorCatalog.RELEASE_NOT_FOUND, "Release not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException releaseCodeExists(String code) {
        return new AppException(QualityErrorCatalog.RELEASE_CODE_EXISTS, "Code exists: " + code, Map.of("code", code == null ? "" : code));
    }
    public static AppException releaseNotReady(String d) {
        return new AppException(QualityErrorCatalog.RELEASE_NOT_READY, d, Map.of());
    }
    public static AppException releaseInvalidStatus(String d) {
        return new AppException(QualityErrorCatalog.RELEASE_INVALID_STATUS, d, Map.of());
    }
    public static AppException deploymentEnvNotFound(UUID id) {
        return new AppException(QualityErrorCatalog.DEPLOYMENT_ENV_NOT_FOUND, "Environment not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException deploymentNotFound(UUID id) {
        return new AppException(QualityErrorCatalog.DEPLOYMENT_NOT_FOUND, "Deployment not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException deploymentInvalidStatus(String d) {
        return new AppException(QualityErrorCatalog.DEPLOYMENT_INVALID_STATUS, d, Map.of());
    }
    public static AppException rollbackPlanNotFound(UUID id) {
        return new AppException(QualityErrorCatalog.ROLLBACK_PLAN_NOT_FOUND, "Rollback plan not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException accessDenied() {
        return new AppException(QualityErrorCatalog.QUALITY_ACCESS_DENIED);
    }
    public static AppException projectArchived(UUID id) {
        return new AppException(QualityErrorCatalog.QUALITY_PROJECT_ARCHIVED, "Project archived: " + id, Map.of("projectId", id));
    }
    public static AppException nameRequired() {
        return new AppException(QualityErrorCatalog.QUALITY_NAME_REQUIRED);
    }
}
