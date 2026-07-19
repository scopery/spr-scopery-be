package com.company.scopery.modules.clientportal.shared.constant;
import com.company.scopery.common.constant.ApiPaths;
public final class ClientPortalApiPaths {
    private static final String PBASE = ApiPaths.BASE_PATH + "/projects/{projectId}";
    public static final String REVIEW_REQUESTS = PBASE + "/client-reviews";
    public static final String ACCESS_GRANTS = PBASE + "/portal-access-grants";
    public static final String FEEDBACK = PBASE + "/client-feedback";
    public static final String UAT_ASSIGNMENTS = PBASE + "/client-uat-assignments";
    public static final String COMMENTS = PBASE + "/client-comments";
    public static final String INVITES = PBASE + "/portal-invites";
    public static final String PORTAL_AUTH = ApiPaths.BASE_PATH + "/portal/auth";
    public static final String PORTAL_PROJECTS = ApiPaths.BASE_PATH + "/portal/projects";
    public static final String PORTAL_SUPPORT_CASES = ApiPaths.BASE_PATH + "/portal/projects/{projectId}/support/cases";
    public static final String PORTAL_ACCOUNTS = ApiPaths.BASE_PATH + "/workspaces/{workspaceId}/portal-accounts";
    public static final String PERMISSION_POLICIES = ApiPaths.BASE_PATH + "/workspaces/{workspaceId}/portal-permission-policies";
    public static final String PORTAL_AUDIT_LOGS = PBASE + "/portal-audit-logs";
    private ClientPortalApiPaths() {}
}
