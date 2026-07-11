package com.company.scopery.modules.notification.emailtrigger.application.service;

import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPayload;
import org.springframework.context.ApplicationEvent;

public class EmailNotificationTrigger extends ApplicationEvent {

    private final EmailNotificationTriggerPayload triggerPayload;

    public EmailNotificationTrigger(Object source, EmailNotificationTriggerPayload triggerPayload) {
        super(source);
        this.triggerPayload = triggerPayload;
    }

    public EmailNotificationTriggerPayload triggerPayload() {
        return triggerPayload;
    }
}
