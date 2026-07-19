package com.company.scopery.modules.scope.shared.constant;
import com.company.scopery.common.constant.ApiPaths;
public final class ScopeApiPaths {
    private static final String BASE = ApiPaths.BASE_PATH + "/projects/{projectId}";
    public static final String PACKAGES = BASE + "/scope-packages";
    public static final String ITEMS = BASE + "/scope-items";
    public static final String DELIVERABLES = BASE + "/deliverables";
    public static final String CRITERIA = BASE + "/acceptance-criteria";
    public static final String REPORTS = BASE + "/reports";
    public static final String REVIEWS = BASE + "/reviews";
    public static final String EVIDENCE_BY_ID = BASE + "/evidence";
    private ScopeApiPaths() {}
}
