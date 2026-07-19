package com.company.scopery.modules.notification.emailrule.application.service;

import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRecipientStrategy;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRule;

import java.util.List;
import java.util.Map;

public interface ExtendedRecipientStrategyHandler {
    boolean supports(EmailRecipientStrategy strategy);

    List<EmailRecipientResolver.RecipientResult> resolveAll(EmailRule rule, Map<String, Object> payload);
}
