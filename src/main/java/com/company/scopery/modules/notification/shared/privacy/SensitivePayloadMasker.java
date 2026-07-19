package com.company.scopery.modules.notification.shared.privacy;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Masks nested payload values at EventVariable-style dotted paths (e.g. invitee.email).
 */
@Component
public class SensitivePayloadMasker {

    public static final String MASK = "***";

    @SuppressWarnings("unchecked")
    public Map<String, Object> mask(Map<String, Object> payload, Set<String> sensitivePaths) {
        if (payload == null || sensitivePaths == null || sensitivePaths.isEmpty()) {
            return payload;
        }
        Map<String, Object> copy = deepCopyMap(payload);
        for (String path : sensitivePaths) {
            if (path == null || path.isBlank()) continue;
            maskPath(copy, path.split("\\."));
        }
        return copy;
    }

    @SuppressWarnings("unchecked")
    private void maskPath(Map<String, Object> node, String[] parts) {
        if (node == null || parts.length == 0) return;
        String key = parts[0];
        if (!node.containsKey(key)) return;
        if (parts.length == 1) {
            node.put(key, MASK);
            return;
        }
        Object child = node.get(key);
        if (child instanceof Map<?, ?> mapChild) {
            maskPath((Map<String, Object>) mapChild, slice(parts, 1));
        }
    }

    private String[] slice(String[] parts, int from) {
        String[] rest = new String[parts.length - from];
        System.arraycopy(parts, from, rest, 0, rest.length);
        return rest;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> deepCopyMap(Map<String, Object> source) {
        Map<String, Object> copy = new HashMap<>();
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map<?, ?> nested) {
                copy.put(entry.getKey(), deepCopyMap((Map<String, Object>) nested));
            } else if (value instanceof List<?> list) {
                copy.put(entry.getKey(), deepCopyList(list));
            } else {
                copy.put(entry.getKey(), value);
            }
        }
        return copy;
    }

    @SuppressWarnings("unchecked")
    private List<Object> deepCopyList(List<?> source) {
        List<Object> copy = new ArrayList<>(source.size());
        for (Object item : source) {
            if (item instanceof Map<?, ?> nested) {
                copy.add(deepCopyMap((Map<String, Object>) nested));
            } else if (item instanceof List<?> list) {
                copy.add(deepCopyList(list));
            } else {
                copy.add(item);
            }
        }
        return copy;
    }
}
