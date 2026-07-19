package com.company.scopery.modules.notification.advanced.shared.util;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

/** Minimal condition JSON evaluator: {"equals":{"field":"status","value":"ACTIVE"}} or blank/null → true. */
public final class SimpleConditionEvaluator {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private SimpleConditionEvaluator() {}

    public static boolean matches(String conditionJson, Map<String, String> context) {
        if (conditionJson == null || conditionJson.isBlank() || "{}".equals(conditionJson.trim())) return true;
        try {
            JsonNode root = MAPPER.readTree(conditionJson);
            if (root.has("equals")) {
                JsonNode eq = root.get("equals");
                String field = eq.path("field").asText(null);
                String expected = eq.path("value").asText(null);
                if (field == null) return false;
                String actual = context == null ? null : context.get(field);
                return expected == null ? actual == null : expected.equals(actual);
            }
            if (root.has("and") && root.get("and").isArray()) {
                for (JsonNode child : root.get("and")) {
                    if (!matches(child.toString(), context)) return false;
                }
                return true;
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
