package com.company.scopery.modules.notification.emailoutbox.infrastructure.provider;

import com.company.scopery.modules.notification.emailoutbox.domain.EmailProviderType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class EmailProviderResolver {

    private final LogOnlyEmailSender logOnlySender;
    private final SmtpEmailSender smtpSender;

    public EmailProviderResolver(LogOnlyEmailSender logOnlySender, @Nullable SmtpEmailSender smtpSender) {
        this.logOnlySender = logOnlySender;
        this.smtpSender = smtpSender;
    }

    public EmailSender resolve(EmailProviderType providerType) {
        return switch (providerType) {
            case LOG_ONLY -> logOnlySender;
            case SMTP -> {
                if (smtpSender == null) throw new UnsupportedOperationException("SMTP provider is not configured");
                yield smtpSender;
            }
            default -> throw new UnsupportedOperationException(
                    "Email provider not implemented in Phase 1: " + providerType);
        };
    }
}
