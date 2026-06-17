package com.company.scopery.modules.notification.emailoutbox.infrastructure.provider;

import com.company.scopery.modules.notification.emailoutbox.domain.EmailMessage;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConditionalOnProperty(name = "spring.mail.host")
public class SmtpEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailSender.class);

    private final JavaMailSender mailSender;

    public SmtpEmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public EmailSendResult send(EmailMessage message, String fromAddress, String fromName) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setFrom(fromAddress, fromName);
            helper.setTo(message.toEmail());
            helper.setSubject(message.subject());
            helper.setText(message.textBody() != null ? message.textBody() : "", message.htmlBody());
            mailSender.send(mime);
            String messageId = UUID.randomUUID().toString();
            log.info("[SMTP] Email sent — to={} subject={} messageId={}", message.toEmail(), message.subject(), messageId);
            return EmailSendResult.ok(messageId);
        } catch (Exception e) {
            log.error("[SMTP] Failed to send email to={} subject={}", message.toEmail(), message.subject());
            return EmailSendResult.fail(e.getMessage());
        }
    }
}
