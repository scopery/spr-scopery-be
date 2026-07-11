package com.company.scopery.modules.notification.emailrule.infrastructure.mapper;

import com.company.scopery.modules.notification.emailrule.domain.model.EmailRule;
import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRecipientStrategy;
import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRuleScope;
import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRuleStatus;
import com.company.scopery.modules.notification.emailrule.infrastructure.persistence.EmailRuleJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class EmailRulePersistenceMapper {

    public EmailRule toDomain(EmailRuleJpaEntity e) {
        return EmailRule.reconstitute(
                e.getId(), e.getCode(), e.getName(), e.getDescription(),
                EmailRuleScope.valueOf(e.getScope()),
                e.getWorkspaceId(), e.getEventDefinitionId(), e.getTemplateId(),
                EmailRecipientStrategy.valueOf(e.getRecipientStrategy()),
                e.getRecipientConfigJson(),
                e.getPriority(), e.isEnabled(),
                EmailRuleStatus.valueOf(e.getStatus()),
                e.getCreatedAt(), e.getUpdatedAt(), e.getDeletedAt());
    }

    public EmailRuleJpaEntity toJpaEntity(EmailRule d) {
        EmailRuleJpaEntity e = new EmailRuleJpaEntity();
        e.setId(d.id());
        e.setCode(d.code());
        e.setName(d.name());
        e.setDescription(d.description());
        e.setScope(d.scope().name());
        e.setWorkspaceId(d.workspaceId());
        e.setEventDefinitionId(d.eventDefinitionId());
        e.setTemplateId(d.templateId());
        e.setRecipientStrategy(d.recipientStrategy().name());
        e.setRecipientConfigJson(d.recipientConfigJson());
        e.setPriority(d.priority());
        e.setEnabled(d.enabled());
        e.setStatus(d.status().name());
        e.setDeletedAt(d.deletedAt());
        e.setCreatedAt(d.createdAt());
        return e;
    }
}
