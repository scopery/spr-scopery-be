package com.company.scopery.modules.notification.emailoutbox.domain;

public record EmailMessage(
        String toEmail,
        String subject,
        String htmlBody,
        String textBody
) {
    public EmailMessage {
        if (toEmail == null || toEmail.isBlank()) throw new IllegalArgumentException("toEmail must not be blank");
        if (subject == null || subject.isBlank()) throw new IllegalArgumentException("subject must not be blank");
        if (htmlBody == null || htmlBody.isBlank()) throw new IllegalArgumentException("htmlBody must not be blank");
    }
}
