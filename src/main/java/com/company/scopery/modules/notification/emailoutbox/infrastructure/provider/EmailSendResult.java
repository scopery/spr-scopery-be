package com.company.scopery.modules.notification.emailoutbox.infrastructure.provider;

public record EmailSendResult(
        boolean success,
        String messageId,
        String failureReason
) {
    public static EmailSendResult ok(String messageId) {
        return new EmailSendResult(true, messageId, null);
    }

    public static EmailSendResult fail(String reason) {
        return new EmailSendResult(false, null, reason);
    }
}
