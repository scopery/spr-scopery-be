package com.company.scopery.modules.iam.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class IamApiPaths {

    private static final String BASE = ApiPaths.BASE_PATH + "/iam";

    public static final String IAM_AUTH             = ApiPaths.IAM_AUTH;
    public static final String IAM_USERS            = ApiPaths.IAM_USERS;
    public static final String IAM_ROLES            = BASE + "/roles";
    public static final String IAM_RIGHTS           = BASE + "/rights";
    public static final String IAM_PERMISSIONS      = BASE + "/permissions";
    public static final String IAM_AUTH_RESOURCES   = BASE + "/resources";
    public static final String IAM_ACCESS_GRANTS    = BASE + "/grants";
    public static final String IAM_ROLE_ASSIGNMENTS = BASE + "/role-assignments";
    public static final String IAM_AUTHORIZATION    = BASE + "/authorization";
    public static final String IAM_ME               = BASE + "/me";
    public static final String IAM_OWNER_POLICIES   = BASE + "/owner-policies";
    public static final String IAM_ACCESS           = BASE + "/access";

    private IamApiPaths() {}
}
