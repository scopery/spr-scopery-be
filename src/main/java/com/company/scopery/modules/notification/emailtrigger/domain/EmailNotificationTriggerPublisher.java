package com.company.scopery.modules.notification.emailtrigger.domain;

public interface EmailNotificationTriggerPublisher {
    void publish(EmailNotificationTriggerPayload payload);
}
