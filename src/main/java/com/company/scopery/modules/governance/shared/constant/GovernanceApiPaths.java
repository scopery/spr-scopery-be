package com.company.scopery.modules.governance.shared.constant;
import com.company.scopery.common.constant.ApiPaths;
public final class GovernanceApiPaths {
    public static final String OBJECT_TYPES = ApiPaths.BASE_PATH + "/governance/object-types";
    public static final String WS_POLICIES = ApiPaths.BASE_PATH + "/workspaces/{workspaceId}/governance/policies";
    public static final String PROJECT = ApiPaths.BASE_PATH + "/projects/{projectId}/governance";
    public static final String REPORTS = PROJECT + "/reports";
    private GovernanceApiPaths(){}
}
