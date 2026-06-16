package com.company.scopery.modules.iam.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class IamApiPaths {

    private static final String BASE = ApiPaths.V1 + "/iam";

    public static final String IAM_AUTH           = BASE + "/auth";
    public static final String IAM_USERS          = BASE + "/users";
    public static final String IAM_ROLES          = BASE + "/roles";
    public static final String IAM_RIGHTS         = BASE + "/rights";
    public static final String IAM_AUTH_RESOURCES   = BASE + "/resources";
    public static final String IAM_ACCESS_GRANTS    = BASE + "/grants";
    public static final String IAM_ROLE_ASSIGNMENTS = BASE + "/role-assignments";
    public static final String IAM_AUTHORIZATION    = BASE + "/authorization";

    private IamApiPaths() {}
}
