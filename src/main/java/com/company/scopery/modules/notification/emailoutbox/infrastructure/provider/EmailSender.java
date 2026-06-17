package com.company.scopery.modules.notification.emailoutbox.infrastructure.provider;

import com.company.scopery.modules.notification.emailoutbox.domain.EmailMessage;

public interface EmailSender {
    EmailSendResult send(EmailMessage message, String fromAddress, String fromName);
}
