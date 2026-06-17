package com.company.scopery.modules.workspace.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class WorkspaceApiPaths {

    private static final String BASE_WORKSPACES = ApiPaths.V1 + "/workspaces";

    public static final String ORGANIZATIONS     = ApiPaths.V1 + "/organizations";
    public static final String WORKSPACES        = BASE_WORKSPACES;
    public static final String WORKSPACE_MEMBERS = BASE_WORKSPACES + "/{workspaceId}/members";
    public static final String TEAMS             = BASE_WORKSPACES + "/{workspaceId}/teams";

    public static final String WORKSPACE_INVITATIONS        = BASE_WORKSPACES + "/{workspaceId}/invitations";
    public static final String WORKSPACE_INVITATION_ACCEPT  = BASE_WORKSPACES + "/invitations/{code}/accept";
    public static final String WORKSPACE_JOIN_REQUESTS      = BASE_WORKSPACES + "/{workspaceId}/join-requests";
    public static final String WORKSPACE_JOIN_REQUESTS_OPEN = ApiPaths.V1 + "/workspace-join-requests";
    public static final String WORKSPACE_CONTEXT            = ApiPaths.V1 + "/workspace-context";
    public static final String WORKSPACE_ONBOARDING         = ApiPaths.V1 + "/workspace-onboarding";

    private WorkspaceApiPaths() {}
}
