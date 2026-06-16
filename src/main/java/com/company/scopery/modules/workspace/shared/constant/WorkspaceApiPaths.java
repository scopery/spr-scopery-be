package com.company.scopery.modules.workspace.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class WorkspaceApiPaths {

    private static final String BASE_WORKSPACES = ApiPaths.V1 + "/workspaces";

    public static final String ORGANIZATIONS     = ApiPaths.V1 + "/organizations";
    public static final String WORKSPACES        = BASE_WORKSPACES;
    public static final String WORKSPACE_MEMBERS = BASE_WORKSPACES + "/{workspaceId}/members";
    public static final String TEAMS             = BASE_WORKSPACES + "/{workspaceId}/teams";

    private WorkspaceApiPaths() {}
}
