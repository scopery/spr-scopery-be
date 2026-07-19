package com.company.scopery.modules.notification.emailoutbox.application.action;

import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.emailoutbox.application.jobs.EmailOutboxProcessor;
import com.company.scopery.modules.notification.emailoutbox.application.response.EmailOutboxResponse;
import com.company.scopery.modules.notification.emailoutbox.domain.enums.EmailOutboxStatus;
import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailOutbox;
import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailOutboxRepository;
import com.company.scopery.modules.notification.shared.NotificationActivityActions;
import com.company.scopery.modules.notification.shared.NotificationActivityLogger;
import com.company.scopery.modules.notification.shared.NotificationEntityTypes;
import com.company.scopery.modules.notification.shared.error.NotificationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RetryEmailOutboxAction {

    private final EmailOutboxRepository outboxRepository;
    private final EmailOutboxProcessor outboxProcessor;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final NotificationActivityLogger activityLogger;

    public RetryEmailOutboxAction(EmailOutboxRepository outboxRepository,
                                   EmailOutboxProcessor outboxProcessor,
                                   IamSystemAuthorizationService systemAuthorizationService,
                                   NotificationActivityLogger activityLogger) {
        this.outboxRepository = outboxRepository;
        this.outboxProcessor = outboxProcessor;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public EmailOutboxResponse execute(UUID id) {
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_NOTIFICATION_RETRY_DELIVERY.legacyRightCode());
        EmailOutbox outbox = findOrThrow(id);

        if (outbox.status() != EmailOutboxStatus.FAILED
                && outbox.status() != EmailOutboxStatus.DEAD_LETTER) {
            throw NotificationExceptions.emailOutboxNotRetryable(id, outbox.status().name());
        }

        outbox.resetForManualRetry();
        outbox = outboxRepository.save(outbox);
        activityLogger.logSuccess(NotificationEntityTypes.EMAIL_OUTBOX, outbox.id(),
                NotificationActivityActions.RETRY_EMAIL,
                "Email outbox manually retried");
        outboxProcessor.processOne(outbox);
        return EmailOutboxResponse.from(findOrThrow(id));
    }

    private EmailOutbox findOrThrow(UUID id) {
        return outboxRepository.findById(id)
                .orElseThrow(() -> NotificationExceptions.emailOutboxNotFound(id));
    }
}
