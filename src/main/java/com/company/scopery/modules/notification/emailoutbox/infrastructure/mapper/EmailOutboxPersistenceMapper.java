package com.company.scopery.modules.notification.emailoutbox.infrastructure.mapper;

import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailOutbox;
import com.company.scopery.modules.notification.emailoutbox.domain.enums.EmailOutboxStatus;
import com.company.scopery.modules.notification.emailoutbox.domain.enums.EmailProviderType;
import com.company.scopery.modules.notification.emailoutbox.infrastructure.persistence.EmailOutboxJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class EmailOutboxPersistenceMapper {

    public EmailOutbox toDomain(EmailOutboxJpaEntity e) {
        return EmailOutbox.reconstitute(
                e.getId(), e.getDeliveryId(),
                e.getToEmail(), e.getSubject(), e.getHtmlBody(), e.getTextBody(),
                EmailProviderType.valueOf(e.getProviderType()),
                e.getDedupKey(),
                EmailOutboxStatus.valueOf(e.getStatus()),
                e.getFailureReason(), e.getProviderMessageId(),
                e.getRetryCount(), e.getScheduledAt(), e.getSentAt(),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public EmailOutboxJpaEntity toJpaEntity(EmailOutbox d) {
        EmailOutboxJpaEntity e = new EmailOutboxJpaEntity();
        e.setId(d.id());
        e.setDeliveryId(d.deliveryId());
        e.setToEmail(d.toEmail());
        e.setSubject(d.subject());
        e.setHtmlBody(d.htmlBody());
        e.setTextBody(d.textBody());
        e.setProviderType(d.providerType().name());
        e.setStatus(d.status().name());
        e.setDedupKey(d.dedupKey());
        e.setProviderMessageId(d.providerMessageId());
        e.setFailureReason(d.failureReason());
        e.setRetryCount(d.retryCount());
        e.setScheduledAt(d.scheduledAt());
        e.setSentAt(d.sentAt());
        e.setCreatedAt(d.createdAt());
        return e;
    }
}
