package com.company.scopery.modules.ratecard.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class RateCardApiPaths {
    private static final String BASE = ApiPaths.BASE_PATH + "/rate-card";

    public static final String COST_ROLES = BASE + "/cost-roles";
    public static final String MEMBER_COST_ROLES = BASE + "/member-cost-roles";
    public static final String CARDS = BASE + "/cards";
    public static final String CARD_VERSIONS = BASE + "/cards/{rateCardId}/versions";
    public static final String CARD_LINES = BASE + "/cards/{rateCardId}/versions/{versionId}/lines";
    public static final String INFLATION_POLICIES = BASE + "/inflation-policies";
    public static final String RESOLVE = BASE + "/resolve";
    public static final String PREVIEW_TASK_RATE = BASE + "/preview-task-rate";

    private RateCardApiPaths() {}
}
