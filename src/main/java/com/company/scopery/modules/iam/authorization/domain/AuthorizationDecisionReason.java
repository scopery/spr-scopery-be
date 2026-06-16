package com.company.scopery.modules.iam.authorization.domain;

public enum AuthorizationDecisionReason {
    // Allow reasons
    OWNER_IMPLICIT_ALLOW,
    USER_GRANT_ALLOW,
    TEAM_GRANT_ALLOW,
    ROLE_GRANT_ALLOW,
    VISIBILITY_ALLOW,

    // Deny reasons
    EXPLICIT_DENY,
    OWNER_CHECK_FAILED,
    USER_NOT_FOUND,
    RIGHT_NOT_FOUND,
    RESOURCE_NOT_FOUND,
    RESOURCE_INACTIVE,
    USER_INACTIVE,
    WORKSPACE_MEMBER_REQUIRED,
    DEFAULT_DENY
}
