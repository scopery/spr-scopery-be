package com.company.scopery.modules.notification.emailoutbox.application.action;

import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
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
public class CancelEmailOutboxAction {

    private final EmailOutboxRepository outboxRepository;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final NotificationActivityLogger activityLogger;

    public CancelEmailOutboxAction(EmailOutboxRepository outboxRepository,
                                    IamSystemAuthorizationService systemAuthorizationService,
                                    NotificationActivityLogger activityLogger) {
        this.outboxRepository = outboxRepository;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public EmailOutboxResponse execute(UUID id) {
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_NOTIFICATION_RETRY_DELIVERY.legacyRightCode());
        EmailOutbox outbox = outboxRepository.findById(id)
                .orElseThrow(() -> NotificationExceptions.emailOutboxNotFound(id));

        if (outbox.status() != EmailOutboxStatus.PENDING
                && outbox.status() != EmailOutboxStatus.RETRY_SCHEDULED
                && outbox.status() != EmailOutboxStatus.FAILED
                && outbox.status() != EmailOutboxStatus.DEAD_LETTER) {
            throw NotificationExceptions.emailOutboxNotCancellable(id, outbox.status().name());
        }

        outbox.markCancelled();
        outbox = outboxRepository.save(outbox);
        activityLogger.logSuccess(NotificationEntityTypes.EMAIL_OUTBOX, outbox.id(),
                NotificationActivityActions.CANCEL_EMAIL,
                "Email outbox cancelled");
        return EmailOutboxResponse.from(outbox);
    }
}
