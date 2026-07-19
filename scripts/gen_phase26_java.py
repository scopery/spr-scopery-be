#!/usr/bin/env python3
"""Generate Phase 26 Java module files (shared + entities + APIs)."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "src/main/java/com/company/scopery/modules/quality"
TEST = ROOT / "src/test/java/com/company/scopery/modules/quality"
PKG = "com.company.scopery.modules.quality"

def w(rel: str, content: str, test=False):
    base = TEST if test else JAVA
    path = base / rel
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content.rstrip() + "\n")
    print(path.relative_to(ROOT))

# ═══════════════ SHARED ═══════════════
w("shared/constant/QualityModuleCodes.java", f"""package {PKG}.shared.constant;
public final class QualityModuleCodes {{
    public static final String QUALITY = "QUALITY";
    private QualityModuleCodes() {{}}
}}
""")

w("shared/constant/QualityTableNames.java", f"""package {PKG}.shared.constant;
public final class QualityTableNames {{
    public static final String QUALITY_PLAN = "project_quality_plan";
    public static final String TEST_PLAN = "project_test_plan";
    public static final String TEST_SUITE = "project_test_suite";
    public static final String TEST_CASE = "project_test_case";
    public static final String TEST_STEP = "project_test_step";
    public static final String TEST_CASE_COVERAGE = "project_test_case_coverage";
    public static final String TEST_RUN = "project_test_run";
    public static final String TEST_CASE_RESULT = "project_test_case_result";
    public static final String TEST_STEP_RESULT = "project_test_step_result";
    public static final String DEFECT = "project_defect";
    public static final String DEFECT_LINK = "project_defect_link";
    public static final String RELEASE_PACKAGE = "project_release_package";
    public static final String RELEASE_ITEM = "project_release_item";
    public static final String RELEASE_READINESS_CHECK = "project_release_readiness_check";
    public static final String DEPLOYMENT_ENVIRONMENT = "project_deployment_environment";
    public static final String DEPLOYMENT_RECORD = "project_deployment_record";
    public static final String ROLLBACK_PLAN = "project_rollback_plan";
    private QualityTableNames() {{}}
}}
""")

w("shared/constant/QualityApiPaths.java", f"""package {PKG}.shared.constant;
import com.company.scopery.common.constant.ApiPaths;
public final class QualityApiPaths {{
    private static final String BASE = ApiPaths.BASE_PATH + "/projects/{{projectId}}";
    public static final String QUALITY_PLANS = BASE + "/quality-plans";
    public static final String TEST_PLANS = BASE + "/test-plans";
    public static final String TEST_SUITES = BASE + "/test-suites";
    public static final String TEST_CASES = BASE + "/test-cases";
    public static final String TEST_RUNS = BASE + "/test-runs";
    public static final String DEFECTS = BASE + "/defects";
    public static final String RELEASES = BASE + "/releases";
    public static final String DEPLOYMENT_ENVS = BASE + "/deployment-environments";
    public static final String DEPLOYMENTS = BASE + "/deployments";
    public static final String ROLLBACK_PLANS = BASE + "/rollback-plans";
    public static final String REPORTS = BASE + "/reports";
    private QualityApiPaths() {{}}
}}
""")

w("shared/constant/QualityEntityTypes.java", f"""package {PKG}.shared.constant;
public final class QualityEntityTypes {{
    public static final String QUALITY_PLAN = "QUALITY_PLAN";
    public static final String TEST_PLAN = "TEST_PLAN";
    public static final String TEST_SUITE = "TEST_SUITE";
    public static final String TEST_CASE = "TEST_CASE";
    public static final String TEST_STEP = "TEST_STEP";
    public static final String TEST_RUN = "TEST_RUN";
    public static final String TEST_CASE_RESULT = "TEST_CASE_RESULT";
    public static final String DEFECT = "DEFECT";
    public static final String DEFECT_LINK = "DEFECT_LINK";
    public static final String RELEASE_PACKAGE = "RELEASE_PACKAGE";
    public static final String RELEASE_ITEM = "RELEASE_ITEM";
    public static final String DEPLOYMENT_ENVIRONMENT = "DEPLOYMENT_ENVIRONMENT";
    public static final String DEPLOYMENT_RECORD = "DEPLOYMENT_RECORD";
    public static final String ROLLBACK_PLAN = "ROLLBACK_PLAN";
    private QualityEntityTypes() {{}}
}}
""")

w("shared/constant/QualityActivityActions.java", f"""package {PKG}.shared.constant;
public final class QualityActivityActions {{
    public static final String QUALITY_PLAN_CREATED = "QUALITY_PLAN_CREATED";
    public static final String QUALITY_PLAN_APPROVED = "QUALITY_PLAN_APPROVED";
    public static final String QUALITY_PLAN_MARKED_CURRENT = "QUALITY_PLAN_MARKED_CURRENT";
    public static final String TEST_PLAN_CREATED = "TEST_PLAN_CREATED";
    public static final String TEST_PLAN_APPROVED = "TEST_PLAN_APPROVED";
    public static final String TEST_SUITE_CREATED = "TEST_SUITE_CREATED";
    public static final String TEST_CASE_CREATED = "TEST_CASE_CREATED";
    public static final String TEST_CASE_APPROVED = "TEST_CASE_APPROVED";
    public static final String TEST_RUN_CREATED = "TEST_RUN_CREATED";
    public static final String TEST_RUN_STARTED = "TEST_RUN_STARTED";
    public static final String TEST_RUN_COMPLETED = "TEST_RUN_COMPLETED";
    public static final String TEST_CASE_RESULT_RECORDED = "TEST_CASE_RESULT_RECORDED";
    public static final String DEFECT_CREATED = "DEFECT_CREATED";
    public static final String DEFECT_TRIAGED = "DEFECT_TRIAGED";
    public static final String DEFECT_ASSIGNED = "DEFECT_ASSIGNED";
    public static final String DEFECT_FIXED = "DEFECT_FIXED";
    public static final String DEFECT_VERIFIED = "DEFECT_VERIFIED";
    public static final String DEFECT_CLOSED = "DEFECT_CLOSED";
    public static final String DEFECT_REOPENED = "DEFECT_REOPENED";
    public static final String RELEASE_PACKAGE_CREATED = "RELEASE_PACKAGE_CREATED";
    public static final String RELEASE_READINESS_CHECKED = "RELEASE_READINESS_CHECKED";
    public static final String RELEASE_READY = "RELEASE_READY";
    public static final String RELEASE_RELEASED = "RELEASE_RELEASED";
    public static final String RELEASE_ROLLED_BACK = "RELEASE_ROLLED_BACK";
    public static final String DEPLOYMENT_RECORD_CREATED = "DEPLOYMENT_RECORD_CREATED";
    public static final String DEPLOYMENT_SUCCEEDED = "DEPLOYMENT_SUCCEEDED";
    public static final String DEPLOYMENT_FAILED = "DEPLOYMENT_FAILED";
    public static final String ROLLBACK_PLAN_APPROVED = "ROLLBACK_PLAN_APPROVED";
    private QualityActivityActions() {{}}
}}
""")

w("shared/error/QualityErrorCatalog.java", f"""package {PKG}.shared.error;
import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;
public enum QualityErrorCatalog implements ErrorCatalog {{
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
    QualityErrorCatalog(String c, String m, HttpStatus s) {{ code=c; defaultMessage=m; httpStatus=s; }}
    @Override public String code() {{ return code; }}
    @Override public String defaultMessage() {{ return defaultMessage; }}
    @Override public HttpStatus httpStatus() {{ return httpStatus; }}
}}
""")

w("shared/error/QualityExceptions.java", f"""package {PKG}.shared.error;
import com.company.scopery.common.exception.AppException;
import java.util.Map; import java.util.UUID;
public final class QualityExceptions {{
    private QualityExceptions() {{}}
    public static AppException qualityPlanNotFound(UUID id) {{
        return new AppException(QualityErrorCatalog.QUALITY_PLAN_NOT_FOUND, "Quality plan not found: " + id, Map.of("id", id == null ? "" : id));
    }}
    public static AppException qualityPlanImmutable(UUID id) {{
        return new AppException(QualityErrorCatalog.QUALITY_PLAN_IMMUTABLE, "Quality plan immutable: " + id, Map.of("id", id));
    }}
    public static AppException testPlanNotFound(UUID id) {{
        return new AppException(QualityErrorCatalog.TEST_PLAN_NOT_FOUND, "Test plan not found: " + id, Map.of("id", id == null ? "" : id));
    }}
    public static AppException testSuiteNotFound(UUID id) {{
        return new AppException(QualityErrorCatalog.TEST_SUITE_NOT_FOUND, "Test suite not found: " + id, Map.of("id", id == null ? "" : id));
    }}
    public static AppException testCaseNotFound(UUID id) {{
        return new AppException(QualityErrorCatalog.TEST_CASE_NOT_FOUND, "Test case not found: " + id, Map.of("id", id == null ? "" : id));
    }}
    public static AppException testCaseCodeExists(String code) {{
        return new AppException(QualityErrorCatalog.TEST_CASE_CODE_EXISTS, "Code exists: " + code, Map.of("code", code == null ? "" : code));
    }}
    public static AppException testCaseImmutable(UUID id) {{
        return new AppException(QualityErrorCatalog.TEST_CASE_IMMUTABLE, "Test case immutable: " + id, Map.of("id", id));
    }}
    public static AppException testStepNotFound(UUID id) {{
        return new AppException(QualityErrorCatalog.TEST_STEP_NOT_FOUND, "Test step not found: " + id, Map.of("id", id == null ? "" : id));
    }}
    public static AppException testRunNotFound(UUID id) {{
        return new AppException(QualityErrorCatalog.TEST_RUN_NOT_FOUND, "Test run not found: " + id, Map.of("id", id == null ? "" : id));
    }}
    public static AppException testRunInvalidStatus(String d) {{
        return new AppException(QualityErrorCatalog.TEST_RUN_INVALID_STATUS, d, Map.of());
    }}
    public static AppException testResultNotFound(UUID id) {{
        return new AppException(QualityErrorCatalog.TEST_RESULT_NOT_FOUND, "Test result not found: " + id, Map.of("id", id == null ? "" : id));
    }}
    public static AppException defectNotFound(UUID id) {{
        return new AppException(QualityErrorCatalog.DEFECT_NOT_FOUND, "Defect not found: " + id, Map.of("id", id == null ? "" : id));
    }}
    public static AppException defectInvalidStatus(String d) {{
        return new AppException(QualityErrorCatalog.DEFECT_INVALID_STATUS, d, Map.of());
    }}
    public static AppException defectResolutionRequired() {{
        return new AppException(QualityErrorCatalog.DEFECT_RESOLUTION_REQUIRED);
    }}
    public static AppException defectReopenReasonRequired() {{
        return new AppException(QualityErrorCatalog.DEFECT_REOPEN_REASON_REQUIRED);
    }}
    public static AppException releaseNotFound(UUID id) {{
        return new AppException(QualityErrorCatalog.RELEASE_NOT_FOUND, "Release not found: " + id, Map.of("id", id == null ? "" : id));
    }}
    public static AppException releaseCodeExists(String code) {{
        return new AppException(QualityErrorCatalog.RELEASE_CODE_EXISTS, "Code exists: " + code, Map.of("code", code == null ? "" : code));
    }}
    public static AppException releaseNotReady(String d) {{
        return new AppException(QualityErrorCatalog.RELEASE_NOT_READY, d, Map.of());
    }}
    public static AppException releaseInvalidStatus(String d) {{
        return new AppException(QualityErrorCatalog.RELEASE_INVALID_STATUS, d, Map.of());
    }}
    public static AppException deploymentEnvNotFound(UUID id) {{
        return new AppException(QualityErrorCatalog.DEPLOYMENT_ENV_NOT_FOUND, "Environment not found: " + id, Map.of("id", id == null ? "" : id));
    }}
    public static AppException deploymentNotFound(UUID id) {{
        return new AppException(QualityErrorCatalog.DEPLOYMENT_NOT_FOUND, "Deployment not found: " + id, Map.of("id", id == null ? "" : id));
    }}
    public static AppException deploymentInvalidStatus(String d) {{
        return new AppException(QualityErrorCatalog.DEPLOYMENT_INVALID_STATUS, d, Map.of());
    }}
    public static AppException rollbackPlanNotFound(UUID id) {{
        return new AppException(QualityErrorCatalog.ROLLBACK_PLAN_NOT_FOUND, "Rollback plan not found: " + id, Map.of("id", id == null ? "" : id));
    }}
    public static AppException accessDenied() {{
        return new AppException(QualityErrorCatalog.QUALITY_ACCESS_DENIED);
    }}
    public static AppException projectArchived(UUID id) {{
        return new AppException(QualityErrorCatalog.QUALITY_PROJECT_ARCHIVED, "Project archived: " + id, Map.of("projectId", id));
    }}
    public static AppException nameRequired() {{
        return new AppException(QualityErrorCatalog.QUALITY_NAME_REQUIRED);
    }}
}}
""")

w("shared/activity/QualityActivityLogger.java", f"""package {PKG}.shared.activity;
import com.company.scopery.common.audit.ActivityLogService;
import {PKG}.shared.constant.QualityModuleCodes;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class QualityActivityLogger {{
    private final ActivityLogService s;
    public QualityActivityLogger(ActivityLogService s) {{ this.s = s; }}
    public void logSuccess(String entityType, UUID entityId, String action, String message) {{
        s.logSuccess(QualityModuleCodes.QUALITY, entityType, entityId == null ? null : entityId.toString(), action, null, null, message, null);
    }}
}}
""")

w("shared/util/QualityEnumParser.java", f"""package {PKG}.shared.util;
import com.company.scopery.common.exception.ValidationException;
public final class QualityEnumParser {{
    private QualityEnumParser() {{}}
    public static <E extends Enum<E>> E parseRequired(Class<E> type, String raw, String field) {{
        if (raw == null || raw.isBlank()) throw new ValidationException(field + " is required");
        try {{ return Enum.valueOf(type, raw.trim().toUpperCase()); }}
        catch (IllegalArgumentException ex) {{ throw new ValidationException("Invalid " + field + ": " + raw); }}
    }}
    public static <E extends Enum<E>> E parseOptional(Class<E> type, String raw, String field) {{
        if (raw == null || raw.isBlank()) return null;
        return parseRequired(type, raw, field);
    }}
}}
""")

w("shared/authorization/QualityAuthorizationService.java", f"""package {PKG}.shared.authorization;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import {PKG}.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class QualityAuthorizationService {{
    private final ProjectWorkspaceAuthorizationService projectAuthorization;
    public QualityAuthorizationService(ProjectWorkspaceAuthorizationService projectAuthorization) {{
        this.projectAuthorization = projectAuthorization;
    }}
    public void requireQualityView(UUID projectId) {{ require(projectId, IamAuthorities.QUALITY_VIEW); }}
    public void requireQualityCreate(UUID projectId) {{ require(projectId, IamAuthorities.QUALITY_CREATE); }}
    public void requireQualityUpdate(UUID projectId) {{ require(projectId, IamAuthorities.QUALITY_UPDATE); }}
    public void requireQualityApprove(UUID projectId) {{ require(projectId, IamAuthorities.QUALITY_APPROVE); }}
    public void requireTestView(UUID projectId) {{ require(projectId, IamAuthorities.TEST_VIEW); }}
    public void requireTestCreate(UUID projectId) {{ require(projectId, IamAuthorities.TEST_CREATE); }}
    public void requireTestUpdate(UUID projectId) {{ require(projectId, IamAuthorities.TEST_UPDATE); }}
    public void requireTestExecute(UUID projectId) {{ require(projectId, IamAuthorities.TEST_EXECUTE); }}
    public void requireDefectView(UUID projectId) {{ require(projectId, IamAuthorities.DEFECT_VIEW); }}
    public void requireDefectCreate(UUID projectId) {{ require(projectId, IamAuthorities.DEFECT_CREATE); }}
    public void requireDefectUpdate(UUID projectId) {{ require(projectId, IamAuthorities.DEFECT_UPDATE); }}
    public void requireDefectResolve(UUID projectId) {{ require(projectId, IamAuthorities.DEFECT_RESOLVE); }}
    public void requireReleaseView(UUID projectId) {{ require(projectId, IamAuthorities.RELEASE_VIEW); }}
    public void requireReleaseCreate(UUID projectId) {{ require(projectId, IamAuthorities.RELEASE_CREATE); }}
    public void requireReleaseUpdate(UUID projectId) {{ require(projectId, IamAuthorities.RELEASE_UPDATE); }}
    public void requireReleaseApprove(UUID projectId) {{ require(projectId, IamAuthorities.RELEASE_APPROVE); }}
    public void requireDeploymentView(UUID projectId) {{ require(projectId, IamAuthorities.DEPLOYMENT_VIEW); }}
    public void requireDeploymentManage(UUID projectId) {{ require(projectId, IamAuthorities.DEPLOYMENT_MANAGE); }}
    private void require(UUID projectId, IamPermissionAction a) {{
        try {{ projectAuthorization.requireProjectPermission(projectId, a); }}
        catch (RuntimeException ex) {{ throw QualityExceptions.accessDenied(); }}
    }}
}}
""")

print("Shared kernel done")
