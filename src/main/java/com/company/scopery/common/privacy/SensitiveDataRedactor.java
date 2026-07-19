package com.company.scopery.common.privacy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Redacts sensitive fields from JSON / map payloads used by activity, audit, outbox, and idempotency caches.
 */
@Component
public class SensitiveDataRedactor {

    public static final String REDACTED = "[REDACTED]";

    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "password",
            "passwordhash",
            "currentpassword",
            "newpassword",
            "oldpassword",
            "accesstoken",
            "refreshtoken",
            "resettoken",
            "token",
            "invitationtoken",
            "invitationcode",
            "authorization",
            "apikey",
            "apisecret",
            "providersecret",
            "secret",
            "clientsecret",
            "rawtoken",
            "cookie"
    );

    private static final Pattern BEARER = Pattern.compile("(?i)bearer\\s+[a-z0-9._\\-]+");

    private final ObjectMapper objectMapper;

    public SensitiveDataRedactor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Object redact(Object value) {
        if (value == null) {
            return null;
        }
        try {
            JsonNode tree = objectMapper.valueToTree(value);
            return objectMapper.treeToValue(redactNode(tree), Object.class);
        } catch (Exception exception) {
            return Map.of("redactionError", true, "message", "payload redacted due to serialization failure");
        }
    }

    public String redactJson(String json) {
        if (json == null || json.isBlank()) {
            return json;
        }
        try {
            JsonNode tree = objectMapper.readTree(json);
            return objectMapper.writeValueAsString(redactNode(tree));
        } catch (Exception exception) {
            return REDACTED;
        }
    }

    public String redactText(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        return BEARER.matcher(text).replaceAll("Bearer " + REDACTED);
    }

    private JsonNode redactNode(JsonNode node) {
        if (node == null || node.isNull()) {
            return node;
        }
        if (node.isObject()) {
            ObjectNode object = (ObjectNode) node.deepCopy();
            Iterator<Map.Entry<String, JsonNode>> fields = object.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                if (isSensitiveKey(entry.getKey())) {
                    object.put(entry.getKey(), REDACTED);
                } else {
                    object.set(entry.getKey(), redactNode(entry.getValue()));
                }
            }
            return object;
        }
        if (node.isArray()) {
            ArrayNode array = objectMapper.createArrayNode();
            for (JsonNode child : node) {
                array.add(redactNode(child));
            }
            return array;
        }
        if (node.isTextual()) {
            return objectMapper.getNodeFactory().textNode(redactText(node.asText()));
        }
        return node;
    }

    private boolean isSensitiveKey(String key) {
        if (key == null) {
            return false;
        }
        String normalized = key.replace("_", "").replace("-", "").toLowerCase(Locale.ROOT);
        if (SENSITIVE_KEYS.contains(normalized)) {
            return true;
        }
        return normalized.contains("password")
                || normalized.contains("secret")
                || normalized.endsWith("token")
                || normalized.contains("apikey");
    }
}
