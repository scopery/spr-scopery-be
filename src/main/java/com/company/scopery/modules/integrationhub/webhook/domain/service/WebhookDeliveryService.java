package com.company.scopery.modules.integrationhub.webhook.domain.service;
public final class WebhookDeliveryService {
    private WebhookDeliveryService(){}
    public static String nextStatus(String current, boolean success, int attempt, int maxAttempts) {
        if (success) return "SENT";
        if (attempt >= maxAttempts) return "DEAD_LETTERED";
        return "RETRYING";
    }
    public static String redactPayload(String payload) {
        if (payload == null) return null;
        return payload.replaceAll("(?i)\"(api[_-]?key|token|secret)\"\\s*:\\s*\"[^\"]*\"", "\"secret\":\"***\"");
    }
}
