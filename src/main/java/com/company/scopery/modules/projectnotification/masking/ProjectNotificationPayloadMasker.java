package com.company.scopery.modules.projectnotification.masking;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Masks finance/quote sensitive values for unauthorized recipients.
 * Used by reminder/bridge enrichment; Phase 06 SensitivePayloadMasker still applies for EventVariable.sensitive paths.
 */
@Component
public class ProjectNotificationPayloadMasker {

    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "plannedRevenue", "directCost", "overhead", "grossMargin", "pbt",
            "quoteAmount", "discount", "marginPercent", "totalAmount", "amount"
    );

    public Map<String, Object> maskIfUnauthorized(Map<String, Object> payload, boolean authorized) {
        if (authorized || payload == null) {
            return payload;
        }
        Map<String, Object> copy = new HashMap<>(payload);
        maskMap(copy);
        copy.put("masked", true);
        copy.put("maskMessage", "Sensitive finance/quote details were hidden because you lack access.");
        return copy;
    }

    @SuppressWarnings("unchecked")
    private void maskMap(Map<String, Object> map) {
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (SENSITIVE_KEYS.contains(e.getKey()) || e.getKey().toLowerCase().contains("margin")
                    || e.getKey().toLowerCase().contains("amount")
                    || e.getKey().toLowerCase().contains("discount")) {
                e.setValue("[REDACTED]");
            } else if (e.getValue() instanceof Map<?, ?> nested) {
                Map<String, Object> child = new HashMap<>();
                nested.forEach((k, v) -> child.put(String.valueOf(k), v));
                maskMap(child);
                e.setValue(child);
            }
        }
    }
}
