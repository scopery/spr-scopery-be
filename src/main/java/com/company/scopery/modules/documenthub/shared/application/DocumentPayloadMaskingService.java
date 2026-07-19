package com.company.scopery.modules.documenthub.shared.application;

import com.company.scopery.modules.trust.sensitivefield.domain.model.SensitiveFieldRegistry;
import com.company.scopery.modules.trust.sensitivefield.domain.model.SensitiveFieldRegistryRepository;
import com.company.scopery.modules.trust.shared.domain.SensitiveFieldMasker;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Applies Phase 38 masking strategies to document payload maps on read paths.
 * Registry-driven when workspace has enabled DOCUMENT_* sensitive fields; otherwise built-in defaults.
 */
@Component
public class DocumentPayloadMaskingService {

    private static final String OBJECT_DOCUMENT = "DOCUMENT";
    private static final String OBJECT_DOCUMENT_HUB = "DOCUMENT_HUB";

    private final SensitiveFieldRegistryRepository sensitiveFields;

    public DocumentPayloadMaskingService(SensitiveFieldRegistryRepository sensitiveFields) {
        this.sensitiveFields = sensitiveFields;
    }

    public Map<String, Object> maskDocumentPayload(Map<String, Object> payload) {
        return maskDocumentPayload(null, payload);
    }

    public Map<String, Object> maskDocumentPayload(UUID workspaceId, Map<String, Object> payload) {
        if (payload == null || payload.isEmpty()) return Map.of();
        Map<String, Object> masked = new LinkedHashMap<>(payload);

        boolean appliedRegistry = false;
        if (workspaceId != null) {
            List<SensitiveFieldRegistry> fields = sensitiveFields.findByWorkspaceId(workspaceId).stream()
                    .filter(SensitiveFieldRegistry::enabled)
                    .filter(f -> OBJECT_DOCUMENT.equalsIgnoreCase(f.objectTypeCode())
                            || OBJECT_DOCUMENT_HUB.equalsIgnoreCase(f.objectTypeCode()))
                    .toList();
            for (SensitiveFieldRegistry field : fields) {
                String key = fieldPathKey(field.fieldPath());
                if (key != null && masked.containsKey(key)) {
                    maskField(masked, key, field.maskingStrategy());
                    appliedRegistry = true;
                }
            }
        }

        if (!appliedRegistry) {
            maskField(masked, "ownerEmail", "MASK_EMAIL");
            maskField(masked, "contactEmail", "MASK_EMAIL");
            maskField(masked, "contactPhone", "MASK_PHONE");
            maskField(masked, "contractValue", "HIDE_FINANCIAL");
            maskField(masked, "description", "REDACT");
        } else if (masked.containsKey("description") && !hasRegistryField(workspaceId, "description")) {
            // Always protect free-text description when registry does not cover it.
            maskField(masked, "description", "REDACT");
        }
        return masked;
    }

    private boolean hasRegistryField(UUID workspaceId, String fieldKey) {
        if (workspaceId == null) return false;
        return sensitiveFields.findByWorkspaceId(workspaceId).stream()
                .filter(SensitiveFieldRegistry::enabled)
                .anyMatch(f -> fieldKey.equalsIgnoreCase(fieldPathKey(f.fieldPath())));
    }

    private static String fieldPathKey(String fieldPath) {
        if (fieldPath == null || fieldPath.isBlank()) return null;
        String path = fieldPath.trim();
        int lastDot = path.lastIndexOf('.');
        return lastDot >= 0 ? path.substring(lastDot + 1) : path;
    }

    private void maskField(Map<String, Object> payload, String key, String strategy) {
        if (!payload.containsKey(key) || payload.get(key) == null) return;
        String raw = String.valueOf(payload.get(key));
        String normalized = strategy == null ? "REDACT" : strategy.trim().toUpperCase(Locale.ROOT);
        payload.put(key, switch (normalized) {
            case "MASK_EMAIL" -> SensitiveFieldMasker.maskEmail(raw);
            case "MASK_PHONE" -> SensitiveFieldMasker.maskPhone(raw);
            case "HIDE_FINANCIAL" -> SensitiveFieldMasker.hideFinancial(raw);
            default -> SensitiveFieldMasker.redact();
        });
    }
}
