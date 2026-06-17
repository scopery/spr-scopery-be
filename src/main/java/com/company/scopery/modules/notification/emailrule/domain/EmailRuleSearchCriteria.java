package com.company.scopery.modules.notification.emailrule.domain;

import java.util.UUID;

public record EmailRuleSearchCriteria(
        String keyword,
        EmailRuleScope scope,
        EmailRuleStatus status,
        UUID workspaceId,
        UUID eventDefinitionId,
        UUID templateId,
        int page,
        int size
) {}
