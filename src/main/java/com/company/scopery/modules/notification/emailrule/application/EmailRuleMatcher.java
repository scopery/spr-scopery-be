package com.company.scopery.modules.notification.emailrule.application;

import com.company.scopery.modules.notification.emailrule.domain.EmailRule;
import com.company.scopery.modules.notification.emailrule.domain.EmailRuleRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class EmailRuleMatcher {

    private final EmailRuleRepository ruleRepository;

    public EmailRuleMatcher(EmailRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public List<EmailRule> matchRules(UUID eventDefinitionId, UUID workspaceId) {
        List<EmailRule> matched = new ArrayList<>();
        matched.addAll(ruleRepository.findActiveSystemRulesForEvent(eventDefinitionId));
        if (workspaceId != null) {
            matched.addAll(ruleRepository.findActiveWorkspaceRulesForEvent(eventDefinitionId, workspaceId));
        }
        matched.sort((a, b) -> Integer.compare(a.priority(), b.priority()));
        return matched;
    }
}
