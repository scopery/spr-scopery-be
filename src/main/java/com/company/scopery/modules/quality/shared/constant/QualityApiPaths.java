package com.company.scopery.modules.quality.shared.constant;
import com.company.scopery.common.constant.ApiPaths;
public final class QualityApiPaths {
    private static final String BASE = ApiPaths.BASE_PATH + "/projects/{projectId}";
    public static final String QUALITY_PLANS = BASE + "/quality-plans";
    public static final String TEST_PLANS = BASE + "/test-plans";
    public static final String TEST_PLAN_SUITES = TEST_PLANS + "/{testPlanId}/suites";
    public static final String TEST_SUITES = BASE + "/test-suites";
    public static final String TEST_CASES = BASE + "/test-cases";
    public static final String TEST_CASE_STEPS = TEST_CASES + "/{testCaseId}/steps";
    public static final String TEST_CASE_COVERAGE = TEST_CASES + "/{testCaseId}/coverage";
    public static final String TEST_RUNS = BASE + "/test-runs";
    public static final String DEFECTS = BASE + "/defects";
    public static final String DEFECT_LINKS = DEFECTS + "/{defectId}/links";
    public static final String RELEASES = BASE + "/releases";
    public static final String RELEASE_ITEMS = RELEASES + "/{releasePackageId}/items";
    public static final String DEPLOYMENT_ENVS = BASE + "/deployment-environments";
    public static final String DEPLOYMENTS = BASE + "/deployments";
    public static final String ROLLBACK_PLANS = BASE + "/rollback-plans";
    public static final String REPORTS = BASE + "/reports";
    private QualityApiPaths() {}
}
