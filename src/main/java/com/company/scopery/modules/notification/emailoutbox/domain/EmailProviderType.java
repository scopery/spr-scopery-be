package com.company.scopery.modules.notification.emailoutbox.domain;

public enum EmailProviderType {
    LOG_ONLY,
    SMTP,
    /** Placeholder — not implemented in Phase 1 */
    SENDGRID,
    /** Placeholder — not implemented in Phase 1 */
    AWS_SES
}
