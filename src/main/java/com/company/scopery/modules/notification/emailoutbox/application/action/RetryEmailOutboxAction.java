package com.company.scopery.modules.notification.emailoutbox.application.action;

import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.emailoutbox.application.jobs.EmailOutboxProcessor;
import com.company.scopery.modules.notification.emailoutbox.application.response.EmailOutboxResponse;
import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailOutbox;
import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailOutboxRepository;
import com.company.scopery.modules.notification.emailoutbox.domain.enums.EmailOutboxStatus;
import com.company.scopery.modules.notification.shared.NotificationProperties;
import com.company.scopery.modules.notification.shared.error.NotificationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RetryEmailOutboxAction {

    private final EmailOutboxRepository outboxRepository;
    private final EmailOutboxProcessor outboxProcessor;
    private final NotificationProperties properties;
    private final IamSystemAuthorizationService systemAuthorizationService;

    public RetryEmailOutboxAction(EmailOutboxRepository outboxRepository,
                                   EmailOutboxProcessor outboxProcessor,
                                   NotificationProperties properties,
                                   IamSystemAuthorizationService systemAuthorizationService) {
        this.outboxRepository = outboxRepository;
        this.outboxProcessor = outboxProcessor;
        this.properties = properties;
        this.systemAuthorizationService = systemAuthorizationService;
    }

    @Transactional
    public EmailOutboxResponse execute(UUID id) {
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_NOTIFICATION_RETRY_DELIVERY.legacyRightCode());
        EmailOutbox outbox = findOrThrow(id);
        int maxRetry = properties.getOutbox().getMaxRetry();

        if (outbox.status() != EmailOutboxStatus.FAILED) {
            throw NotificationExceptions.emailOutboxNotRetryable(id, outbox.status().name());
        }
        if (outbox.retryCount() >= maxRetry) {
            throw NotificationExceptions.emailOutboxMaxRetryReached(id, maxRetry);
        }

        outbox.scheduleRetry(0);
        outbox = outboxRepository.save(outbox);
        outboxProcessor.processOne(outbox);
        return EmailOutboxResponse.from(findOrThrow(id));
    }

    private EmailOutbox findOrThrow(UUID id) {
        return outboxRepository.findById(id)
                .orElseThrow(() -> NotificationExceptions.emailOutboxNotFound(id));
    }
}
