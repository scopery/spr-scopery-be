package com.company.scopery.modules.notification.emailrule.domain;

public enum EmailRecipientStrategy {
    EVENT_ACTOR,
    EVENT_TARGET_USER,
    INVITEE_EMAIL,
    REQUESTER_EMAIL,
    STATIC_EMAIL,
    WORKSPACE_OWNER,
    /**
     * Resolves recipients by checking users who hold a specific right in the workspace.
     * The right code must be provided in recipientConfigJson: {"rightCode": "MANAGE_MEMBER"}.
     * Does not hard-code any role name — authorization is right-based via IAM grants.
     * Phase 1: marked as UNSUPPORTED — resolver returns SKIPPED with explanation.
     * Full implementation requires IAM grant resolution across user/team/role subjects.
     */
    WORKSPACE_USERS_WITH_RIGHT
}
