package com.company.scopery.modules.notification.emailoutbox.application.jobs;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.modules.notification.emaildelivery.domain.model.EmailDelivery;
import com.company.scopery.modules.notification.emaildelivery.domain.model.EmailDeliveryRepository;
import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailMessage;
import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailOutbox;
import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailOutboxRepository;
import com.company.scopery.modules.notification.emailoutbox.infrastructure.provider.EmailProviderResolver;
import com.company.scopery.modules.notification.emailoutbox.infrastructure.provider.EmailSendResult;
import com.company.scopery.modules.notification.emailoutbox.infrastructure.provider.EmailSender;
import com.company.scopery.modules.notification.shared.NotificationActivityActions;
import com.company.scopery.modules.notification.shared.NotificationActivityLogger;
import com.company.scopery.modules.notification.shared.NotificationEntityTypes;
import com.company.scopery.modules.notification.shared.NotificationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
public class EmailOutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(EmailOutboxProcessor.class);

    private final EmailOutboxRepository outboxRepository;
    private final EmailDeliveryRepository deliveryRepository;
    private final EmailProviderResolver providerResolver;
    private final NotificationProperties properties;
    private final NotificationActivityLogger activityLogger;
    private final ImmutableAuditEventService auditEventService;

    public EmailOutboxProcessor(EmailOutboxRepository outboxRepository,
                                 EmailDeliveryRepository deliveryRepository,
                                 EmailProviderResolver providerResolver,
                                 NotificationProperties properties,
                                 NotificationActivityLogger activityLogger,
                                 ImmutableAuditEventService auditEventService) {
        this.outboxRepository = outboxRepository;
        this.deliveryRepository = deliveryRepository;
        this.providerResolver = providerResolver;
        this.properties = properties;
        this.activityLogger = activityLogger;
        this.auditEventService = auditEventService;
    }

    @Scheduled(fixedDelayString = "${notification.email.outbox.fixed-delay-ms:10000}")
    public void processPendingOutbox() {
        if (!properties.getOutbox().isEnabled()) return;

        int batchSize = properties.getOutbox().getBatchSize();
        List<EmailOutbox> batch = outboxRepository.findPendingBatch(Instant.now(), batchSize);

        if (batch.isEmpty()) return;

        log.debug("[EmailOutboxProcessor] Processing {} pending outbox records", batch.size());
        for (EmailOutbox outbox : batch) {
            processOne(outbox);
        }
    }

    @Transactional
    public void processOne(EmailOutbox outbox) {
        int claimed = outboxRepository.claimForProcessing(outbox.id());
        if (claimed == 0) {
            log.debug("[EmailOutboxProcessor] Outbox {} already claimed by another process, skipping", outbox.id());
            return;
        }
        outbox.markProcessing();

        try {
            EmailSender sender = providerResolver.resolve(outbox.providerType());
            EmailMessage message = new EmailMessage(
                    outbox.toEmail(), outbox.subject(), outbox.htmlBody(), outbox.textBody());
            EmailSendResult result = sender.send(message, properties.getFromAddress(), properties.getFromName());

            if (result.success()) {
                outbox.markSent(result.messageId());
                outboxRepository.save(outbox);
                updateDeliverySent(outbox);
                log.info("[EmailOutboxProcessor] Sent outbox {} via {}", outbox.id(), outbox.providerType());
            } else {
                handleFailure(outbox, result.failureReason());
            }
        } catch (Exception e) {
            handleFailure(outbox, e.getMessage());
        }
    }

    private void handleFailure(EmailOutbox outbox, String reason) {
        int maxRetry = properties.getOutbox().getMaxRetry();
        if (outbox.retryCount() < maxRetry) {
            outbox.scheduleRetry(properties.getOutbox().getRetryDelaySeconds());
            outboxRepository.save(outbox);
            log.warn("[EmailOutboxProcessor] Outbox {} failed (retry {}/{}): {}",
                    outbox.id(), outbox.retryCount(), maxRetry, reason);
        } else {
            outbox.markDeadLetter(reason);
            outboxRepository.save(outbox);
            updateDeliveryFailed(outbox, reason);
            activityLogger.logSuccess(NotificationEntityTypes.EMAIL_OUTBOX, outbox.id(),
                    NotificationActivityActions.EMAIL_DELIVERY_DEAD_LETTERED,
                    "Email outbox dead-lettered after max retries");
            auditEventService.record(AuditEventType.EMAIL_DELIVERY_DEAD_LETTERED, null, "SYSTEM",
                    NotificationEntityTypes.EMAIL_OUTBOX, outbox.id(), null, null,
                    null, Map.of("reason", reason == null ? "" : reason, "retryCount", outbox.retryCount()),
                    reason);
            log.error("[EmailOutboxProcessor] Outbox {} dead-lettered after {} retries: {}",
                    outbox.id(), maxRetry, reason);
        }
    }

    private void updateDeliverySent(EmailOutbox outbox) {
        deliveryRepository.findById(outbox.deliveryId()).ifPresent(d -> {
            d.markSent();
            deliveryRepository.save(d);
        });
    }

    private void updateDeliveryFailed(EmailOutbox outbox, String reason) {
        deliveryRepository.findById(outbox.deliveryId()).ifPresent(d -> {
            d.markFailed(reason);
            deliveryRepository.save(d);
        });
    }
}
