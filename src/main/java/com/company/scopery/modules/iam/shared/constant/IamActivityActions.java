package com.company.scopery.modules.iam.shared.constant;

public final class IamActivityActions {

    private IamActivityActions() {}

    public static final String CREATE_IAM_USER      = "CREATE_IAM_USER";
    public static final String UPDATE_IAM_USER      = "UPDATE_IAM_USER";
    public static final String ACTIVATE_IAM_USER    = "ACTIVATE_IAM_USER";
    public static final String DEACTIVATE_IAM_USER  = "DEACTIVATE_IAM_USER";
    public static final String SUSPEND_IAM_USER     = "SUSPEND_IAM_USER";

    public static final String CREATE_IAM_ROLE           = "CREATE_IAM_ROLE";
    public static final String CREATE_SYSTEM_ROLE        = "CREATE_SYSTEM_ROLE";
    public static final String CREATE_WORKSPACE_ROLE     = "CREATE_WORKSPACE_ROLE";
    public static final String UPDATE_IAM_ROLE           = "UPDATE_IAM_ROLE";
    public static final String ACTIVATE_IAM_ROLE         = "ACTIVATE_IAM_ROLE";
    public static final String DEACTIVATE_IAM_ROLE       = "DEACTIVATE_IAM_ROLE";
    public static final String SOFT_DELETE_IAM_ROLE      = "SOFT_DELETE_IAM_ROLE";
    public static final String COPY_ROLE_TEMPLATE        = "COPY_ROLE_TEMPLATE";

    public static final String SEED_IAM_RIGHT       = "SEED_IAM_RIGHT";

    public static final String CREATE_IAM_AUTH_RESOURCE     = "CREATE_IAM_AUTH_RESOURCE";
    public static final String UPDATE_IAM_AUTH_RESOURCE     = "UPDATE_IAM_AUTH_RESOURCE";
    public static final String ACTIVATE_IAM_AUTH_RESOURCE   = "ACTIVATE_IAM_AUTH_RESOURCE";
    public static final String DEACTIVATE_IAM_AUTH_RESOURCE = "DEACTIVATE_IAM_AUTH_RESOURCE";

    public static final String CREATE_IAM_ACCESS_GRANT  = "CREATE_IAM_ACCESS_GRANT";
    public static final String REVOKE_IAM_ACCESS_GRANT  = "REVOKE_IAM_ACCESS_GRANT";
    public static final String ADD_IAM_GRANT_RIGHT      = "ADD_IAM_GRANT_RIGHT";
    public static final String REMOVE_IAM_GRANT_RIGHT   = "REMOVE_IAM_GRANT_RIGHT";

    public static final String ASSIGN_IAM_ROLE                  = "ASSIGN_IAM_ROLE";
    public static final String ACTIVATE_IAM_ROLE_ASSIGNMENT     = "ACTIVATE_IAM_ROLE_ASSIGNMENT";
    public static final String DEACTIVATE_IAM_ROLE_ASSIGNMENT   = "DEACTIVATE_IAM_ROLE_ASSIGNMENT";
    public static final String CHECK_AUTHORIZATION               = "CHECK_AUTHORIZATION";
}
