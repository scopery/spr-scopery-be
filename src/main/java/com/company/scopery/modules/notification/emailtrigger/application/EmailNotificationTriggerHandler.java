package com.company.scopery.modules.notification.emailtrigger.application;

import com.company.scopery.modules.notification.emaildelivery.application.EmailDispatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EmailNotificationTriggerHandler {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationTriggerHandler.class);

    private final EmailDispatchService dispatchService;

    public EmailNotificationTriggerHandler(EmailDispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTrigger(EmailNotificationTrigger event) {
        try {
            dispatchService.dispatch(event.triggerPayload());
        } catch (Exception e) {
            log.error("[EmailTriggerHandler] Dispatch failed for event {}: {}",
                    event.triggerPayload().eventDefinitionId(), e.getMessage());
        }
    }
}
