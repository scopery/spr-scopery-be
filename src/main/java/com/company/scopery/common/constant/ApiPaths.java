package com.company.scopery.common.constant;

public final class ApiPaths {

    public static final String BASE_PATH = "/api";

    public static final String HEALTH    = BASE_PATH + "/health";
    public static final String IAM_AUTH  = BASE_PATH + "/iam/auth";
    public static final String IAM_USERS = BASE_PATH + "/iam/users";
    public static final String IAM_AUTH_PASSWORD_RESET_REQUEST = IAM_AUTH + "/password/reset-request";
    public static final String IAM_AUTH_PASSWORD_RESET_CONFIRM = IAM_AUTH + "/password/reset-confirm";

    private ApiPaths() {}
}
