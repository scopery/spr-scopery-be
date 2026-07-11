package com.company.scopery.modules.notification.emailtrigger.application.service;

import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class DefaultEmailNotificationTriggerPublisher implements EmailNotificationTriggerPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public DefaultEmailNotificationTriggerPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publish(EmailNotificationTriggerPayload payload) {
        eventPublisher.publishEvent(new EmailNotificationTrigger(this, payload));
    }
}
