package com.company.scopery.modules.notification.emailtemplate.domain;

import java.util.UUID;

public record EmailTemplateSearchCriteria(
        String keyword,
        EmailTemplateScope scope,
        EmailTemplateStatus status,
        UUID workspaceId,
        UUID eventDefinitionId,
        int page,
        int size
) {}
