package com.company.scopery.modules.projectnotification.masking;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProjectNotificationPayloadMaskerTest {

    private final ProjectNotificationPayloadMasker masker = new ProjectNotificationPayloadMasker();

    @Test
    void masksFinanceWhenUnauthorized() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("project", Map.of("name", "ABC"));
        payload.put("grossMargin", 18.5);
        payload.put("quoteAmount", 1_000_000);
        Map<String, Object> masked = masker.maskIfUnauthorized(payload, false);
        assertEquals("[REDACTED]", masked.get("grossMargin"));
        assertEquals("[REDACTED]", masked.get("quoteAmount"));
        assertEquals(true, masked.get("masked"));
    }

    @Test
    void keepsValuesWhenAuthorized() {
        Map<String, Object> payload = Map.of("grossMargin", 18.5);
        assertSame(payload, masker.maskIfUnauthorized(payload, true));
    }
}
