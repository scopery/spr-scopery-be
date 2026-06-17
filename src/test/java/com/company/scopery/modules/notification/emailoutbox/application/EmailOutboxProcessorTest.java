package com.company.scopery.modules.notification.emailoutbox.application;

import com.company.scopery.modules.notification.emaildelivery.domain.EmailDelivery;
import com.company.scopery.modules.notification.emaildelivery.domain.EmailDeliveryRepository;
import com.company.scopery.modules.notification.emailoutbox.domain.*;
import com.company.scopery.modules.notification.emailoutbox.infrastructure.provider.*;
import com.company.scopery.modules.notification.shared.NotificationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailOutboxProcessorTest {

    @Mock private EmailOutboxRepository outboxRepository;
    @Mock private EmailDeliveryRepository deliveryRepository;
    @Mock private EmailProviderResolver providerResolver;
    @Mock private EmailSender emailSender;

    private NotificationProperties properties;
    private EmailOutboxProcessor processor;

    @BeforeEach
    void setUp() {
        properties = new NotificationProperties();
        properties.getOutbox().setMaxRetry(3);
        properties.getOutbox().setRetryDelaySeconds(60);
        processor = new EmailOutboxProcessor(outboxRepository, deliveryRepository, providerResolver, properties);
    }

    @Test
    void processOne_logOnlySender_marksSent() {
        EmailOutbox outbox = makeOutbox();
        when(outboxRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(providerResolver.resolve(EmailProviderType.LOG_ONLY)).thenReturn(emailSender);
        when(emailSender.send(any(), any(), any())).thenReturn(EmailSendResult.ok("MSG-123"));
        when(deliveryRepository.findById(any())).thenReturn(Optional.of(makeDelivery(outbox.deliveryId())));

        processor.processOne(outbox);

        assertThat(outbox.status()).isEqualTo(EmailOutboxStatus.SENT);
        assertThat(outbox.providerMessageId()).isEqualTo("MSG-123");
    }

    @Test
    void processOne_senderFailure_schedulesRetry() {
        EmailOutbox outbox = makeOutbox();
        when(outboxRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(providerResolver.resolve(any())).thenReturn(emailSender);
        when(emailSender.send(any(), any(), any())).thenReturn(EmailSendResult.fail("connection refused"));

        processor.processOne(outbox);

        assertThat(outbox.status()).isEqualTo(EmailOutboxStatus.PENDING);
        assertThat(outbox.retryCount()).isEqualTo(1);
    }

    @Test
    void processOne_maxRetryReached_marksFailed() {
        EmailOutbox outbox = makeOutboxWithRetries(3);
        when(outboxRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(providerResolver.resolve(any())).thenReturn(emailSender);
        when(emailSender.send(any(), any(), any())).thenReturn(EmailSendResult.fail("timeout"));
        when(deliveryRepository.findById(any())).thenReturn(Optional.of(makeDelivery(outbox.deliveryId())));

        processor.processOne(outbox);

        assertThat(outbox.status()).isEqualTo(EmailOutboxStatus.FAILED);
    }

    private EmailOutbox makeOutbox() {
        return EmailOutbox.create(UUID.randomUUID(),
                new EmailMessage("to@example.com", "Subject", "<p>Body</p>", "Body"),
                EmailProviderType.LOG_ONLY);
    }

    private EmailOutbox makeOutboxWithRetries(int retryCount) {
        return EmailOutbox.reconstitute(
                UUID.randomUUID(), UUID.randomUUID(),
                "to@example.com", "Subject", "<p>Body</p>", "Body",
                EmailProviderType.LOG_ONLY, EmailOutboxStatus.PENDING,
                null, null, retryCount, Instant.now(), null,
                Instant.now(), Instant.now());
    }

    private EmailDelivery makeDelivery(UUID id) {
        return EmailDelivery.reconstitute(id, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), null, "to@example.com", "Subject", "<p>Body</p>", null, null,
                com.company.scopery.modules.notification.emaildelivery.domain.EmailDeliveryStatus.CREATED,
                null, Instant.now(), Instant.now());
    }
}
