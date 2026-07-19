package com.company.scopery.modules.raid.shared.constant;
import com.company.scopery.common.constant.ApiPaths;
public final class RaidApiPaths {
    private static final String BASE = ApiPaths.BASE_PATH + "/projects/{projectId}";
    public static final String ITEMS = BASE + "/raid-items";
    public static final String ACTIONS = BASE + "/raid-actions";
    public static final String DECISIONS = BASE + "/decisions";
    public static final String REPORTS = BASE + "/reports";
    private RaidApiPaths() {}
}
