package com.company.scopery.modules.workspace.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class WorkspaceApiPaths {

    private static final String BASE_WORKSPACES = ApiPaths.BASE_PATH + "/workspaces";
    private static final String BASE_ORGS       = ApiPaths.BASE_PATH + "/organizations";
    private static final String BASE_ORG_TEAMS  = BASE_ORGS + "/{organizationId}/teams";

    public static final String ORGANIZATIONS              = BASE_ORGS;
    public static final String WORKSPACES                 = BASE_WORKSPACES;
    public static final String WORKSPACE_MEMBERS          = BASE_WORKSPACES + "/{workspaceId}/members";
    public static final String TEAMS                      = BASE_WORKSPACES + "/{workspaceId}/teams";

    public static final String WORKSPACE_INVITATIONS        = BASE_WORKSPACES + "/{workspaceId}/invitations";
    public static final String WORKSPACE_INVITATION_ACCEPT  = BASE_WORKSPACES + "/invitations/{code}/accept";
    public static final String WORKSPACE_JOIN_REQUESTS      = BASE_WORKSPACES + "/{workspaceId}/join-requests";
    public static final String WORKSPACE_JOIN_REQUESTS_OPEN = ApiPaths.BASE_PATH + "/workspace-join-requests";
    public static final String WORKSPACE_CONTEXT            = ApiPaths.BASE_PATH + "/workspace-context";
    public static final String WORKSPACE_ONBOARDING         = ApiPaths.BASE_PATH + "/workspace-onboarding";

    public static final String ORG_MEMBERS           = BASE_ORGS + "/{organizationId}/members";
    public static final String ORG_INVITATIONS       = BASE_ORGS + "/{organizationId}/invitations";
    public static final String ORG_INVITATION_ACCEPT = ApiPaths.BASE_PATH + "/org-invitations/{token}/accept";

    public static final String ORG_TEAMS                      = BASE_ORG_TEAMS;
    public static final String ORG_TEAM_MEMBERS               = BASE_ORG_TEAMS + "/{teamId}/members";
    public static final String ORG_TEAM_WORKSPACE_ASSIGNMENTS = BASE_ORG_TEAMS + "/{teamId}/workspace-assignments";

    public static final String ME_WORKSPACES              = ApiPaths.BASE_PATH + "/me/workspaces";
    public static final String WORKSPACE_ACCESS           = BASE_WORKSPACES + "/{workspaceId}/access";
    public static final String WORKSPACE_ACCESS_EXPLAIN   = WORKSPACE_ACCESS + "/{subjectType}/{subjectId}/explain";

    private WorkspaceApiPaths() {}
}
