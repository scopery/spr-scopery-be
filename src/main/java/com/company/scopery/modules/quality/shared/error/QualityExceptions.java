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
    public static AppException testCaseStepNotFound(UUID id) {
        return new AppException(QualityErrorCatalog.TEST_CASE_STEP_NOT_FOUND, "Test case step not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException staleVersion() {
        return new AppException(QualityErrorCatalog.STALE_VERSION);
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
    public static AppException verificationCaseNotFound(UUID id) {
        return new AppException(QualityErrorCatalog.VERIFICATION_CASE_NOT_FOUND, "Verification case not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException verificationCaseAlreadyArchived(UUID id) {
        return new AppException(QualityErrorCatalog.VERIFICATION_CASE_ARCHIVED, "Verification case already archived: " + id, Map.of("id", id));
    }
    public static AppException nfrSpecificationNotFound(UUID requirementId) {
        return new AppException(QualityErrorCatalog.NFR_SPECIFICATION_NOT_FOUND, "NFR specification not found for requirement: " + requirementId, Map.of("requirementId", requirementId == null ? "" : requirementId));
    }
    public static AppException verificationResultNotFound(UUID id) {
        return new AppException(QualityErrorCatalog.VERIFICATION_RESULT_NOT_FOUND, "Verification result not found: " + id, Map.of("id", id == null ? "" : id));
    }
    public static AppException runMembershipScopeMismatch(String caseKind, String runScope) {
        return new AppException(QualityErrorCatalog.RUN_MEMBERSHIP_SCOPE_MISMATCH, "Case kind " + caseKind + " not allowed for run scope " + runScope, Map.of("caseKind", caseKind, "runScope", runScope));
    }
    public static AppException runMembershipRunClosed(UUID runId) {
        return new AppException(QualityErrorCatalog.RUN_MEMBERSHIP_RUN_CLOSED, "Cannot modify membership of a completed or cancelled run: " + runId, Map.of("runId", runId));
    }
    public static AppException runMembershipCaseNotFound(UUID caseId) {
        return new AppException(QualityErrorCatalog.RUN_MEMBERSHIP_CASE_NOT_FOUND, "Case not found in this project: " + caseId, Map.of("caseId", caseId == null ? "" : caseId));
    }
    public static AppException defectSourceResultNotFound(UUID resultId) {
        return new AppException(QualityErrorCatalog.DEFECT_SOURCE_NOT_FOUND, "Source result not found: " + resultId, Map.of("resultId", resultId == null ? "" : resultId));
    }
}
