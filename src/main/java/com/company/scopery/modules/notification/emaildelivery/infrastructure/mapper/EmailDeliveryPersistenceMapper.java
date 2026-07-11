package com.company.scopery.modules.notification.emaildelivery.infrastructure.mapper;

import com.company.scopery.modules.notification.emaildelivery.domain.model.EmailDelivery;
import com.company.scopery.modules.notification.emaildelivery.domain.enums.EmailDeliveryStatus;
import com.company.scopery.modules.notification.emaildelivery.infrastructure.persistence.EmailDeliveryJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class EmailDeliveryPersistenceMapper {

    public EmailDelivery toDomain(EmailDeliveryJpaEntity e) {
        return EmailDelivery.reconstitute(
                e.getId(), e.getRuleId(), e.getTemplateId(), e.getTemplateVersionId(),
                e.getEventDefinitionId(), e.getWorkspaceId(),
                e.getToEmail(), e.getRenderedSubject(), e.getRenderedHtmlBody(), e.getRenderedTextBody(),
                e.getEventPayloadJson(),
                EmailDeliveryStatus.valueOf(e.getStatus()),
                e.getFailureReason(),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public EmailDeliveryJpaEntity toJpaEntity(EmailDelivery d) {
        EmailDeliveryJpaEntity e = new EmailDeliveryJpaEntity();
        e.setId(d.id());
        e.setRuleId(d.ruleId());
        e.setTemplateId(d.templateId());
        e.setTemplateVersionId(d.templateVersionId());
        e.setEventDefinitionId(d.eventDefinitionId());
        e.setWorkspaceId(d.workspaceId());
        e.setToEmail(d.toEmail());
        e.setRenderedSubject(d.renderedSubject());
        e.setRenderedHtmlBody(d.renderedHtmlBody());
        e.setRenderedTextBody(d.renderedTextBody());
        e.setEventPayloadJson(d.eventPayloadJson());
        e.setStatus(d.status().name());
        e.setFailureReason(d.failureReason());
        e.setCreatedAt(d.createdAt());
        return e;
    }
}
