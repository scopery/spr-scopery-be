package com.company.scopery.modules.notification.emailoutbox.domain.model;

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
        if (containsControlCharacters(toEmail)) throw new IllegalArgumentException("toEmail must not contain control characters");
        if (containsControlCharacters(subject)) throw new IllegalArgumentException("subject must not contain control characters");
    }

    private static boolean containsControlCharacters(String value) {
        return value.chars().anyMatch(c -> c == '\r' || c == '\n');
    }
}
