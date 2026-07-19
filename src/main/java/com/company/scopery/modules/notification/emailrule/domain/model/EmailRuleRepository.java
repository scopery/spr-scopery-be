package com.company.scopery.modules.notification.emailrule.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmailRuleRepository {

    EmailRule save(EmailRule rule);

    Optional<EmailRule> findById(UUID id);

    Optional<EmailRule> findByCode(String code);

    boolean existsByCode(String code);

    List<EmailRule> findAll(EmailRuleSearchCriteria criteria);

    long countAll(EmailRuleSearchCriteria criteria);

    List<EmailRule> findActiveSystemRulesForEvent(UUID eventDefinitionId);

    List<EmailRule> findActiveWorkspaceRulesForEvent(UUID eventDefinitionId, UUID workspaceId);

    boolean existsActiveEnabledByEventDefinitionId(UUID eventDefinitionId);
}
