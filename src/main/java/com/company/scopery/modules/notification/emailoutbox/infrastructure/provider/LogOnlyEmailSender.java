package com.company.scopery.modules.notification.emailoutbox.infrastructure.provider;

import com.company.scopery.modules.notification.emailoutbox.domain.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LogOnlyEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(LogOnlyEmailSender.class);

    @Override
    public EmailSendResult send(EmailMessage message, String fromAddress, String fromName) {
        String messageId = "LOG-" + UUID.randomUUID();
        log.info("[LOG_ONLY] Email sent — from={} to={} subject={} messageId={}",
                fromAddress, message.toEmail(), message.subject(), messageId);
        return EmailSendResult.ok(messageId);
    }
}
