package com.company.scopery.modules.notification.emailoutbox.application;

import com.company.scopery.modules.notification.emaildelivery.domain.EmailDelivery;
import com.company.scopery.modules.notification.emaildelivery.domain.EmailDeliveryRepository;
import com.company.scopery.modules.notification.emailoutbox.domain.EmailMessage;
import com.company.scopery.modules.notification.emailoutbox.domain.EmailOutbox;
import com.company.scopery.modules.notification.emailoutbox.domain.EmailOutboxRepository;
import com.company.scopery.modules.notification.emailoutbox.domain.EmailOutboxStatus;
import com.company.scopery.modules.notification.emailoutbox.infrastructure.provider.EmailProviderResolver;
import com.company.scopery.modules.notification.emailoutbox.infrastructure.provider.EmailSendResult;
import com.company.scopery.modules.notification.emailoutbox.infrastructure.provider.EmailSender;
import com.company.scopery.modules.notification.shared.NotificationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class EmailOutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(EmailOutboxProcessor.class);

    private final EmailOutboxRepository outboxRepository;
    private final EmailDeliveryRepository deliveryRepository;
    private final EmailProviderResolver providerResolver;
    private final NotificationProperties properties;

    public EmailOutboxProcessor(EmailOutboxRepository outboxRepository,
                                 EmailDeliveryRepository deliveryRepository,
                                 EmailProviderResolver providerResolver,
                                 NotificationProperties properties) {
        this.outboxRepository = outboxRepository;
        this.deliveryRepository = deliveryRepository;
        this.providerResolver = providerResolver;
        this.properties = properties;
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
        outbox.markProcessing();
        outboxRepository.save(outbox);

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
            outbox.markFailed(reason);
            outboxRepository.save(outbox);
            updateDeliveryFailed(outbox, reason);
            log.error("[EmailOutboxProcessor] Outbox {} permanently failed after {} retries: {}",
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
