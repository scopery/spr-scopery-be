package com.company.scopery.modules.notification.emailtemplate.domain.model;

import com.company.scopery.modules.notification.emailtemplate.domain.enums.EmailTemplateScope;
import com.company.scopery.modules.notification.emailtemplate.domain.enums.EmailTemplateStatus;

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
