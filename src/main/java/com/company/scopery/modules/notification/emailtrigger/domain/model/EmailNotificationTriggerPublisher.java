package com.company.scopery.modules.notification.emailtrigger.domain.model;

public interface EmailNotificationTriggerPublisher {
    void publish(EmailNotificationTriggerPayload payload);
}
