package com.company.scopery.modules.aiagent.execution.application.service;

import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class AiExecutionSchemaValidator {
    private static final Pattern VARIABLE = Pattern.compile("\\{\\{\\s*([A-Za-z0-9_.-]+)\\s*}}" );
    private final ObjectMapper objectMapper;
    public AiExecutionSchemaValidator(ObjectMapper objectMapper) { this.objectMapper = objectMapper; }

    public void validateInput(String eventInputSchema, String promptVariableSchema,
                              String promptContent, Map<String, String> input) {
        Map<String, String> safeInput = input == null ? Map.of() : input;
        if (eventInputSchema != null && !eventInputSchema.isBlank()) {
            validateMapAgainstSchema(eventInputSchema, safeInput);
        }
        if (promptVariableSchema != null && !promptVariableSchema.isBlank()) {
            validateMapAgainstSchema(promptVariableSchema, safeInput);
        } else {
            Set<String> expected = new HashSet<>();
            var matcher = VARIABLE.matcher(promptContent == null ? "" : promptContent);
            while (matcher.find()) expected.add(matcher.group(1));
            Set<String> unknown = new HashSet<>(safeInput.keySet()); unknown.removeAll(expected);
            Set<String> missing = new HashSet<>(expected); missing.removeAll(safeInput.keySet());
            if (!unknown.isEmpty()) throw AiAgentExceptions.invalidExecutionInput("unknown variables " + unknown);
            if (!missing.isEmpty()) throw AiAgentExceptions.invalidExecutionInput("missing variables " + missing);
        }
    }

    public void validateOutput(String outputSchema, String output) {
        if (outputSchema == null || outputSchema.isBlank()) return;
        try {
            JsonNode schema = objectMapper.readTree(outputSchema);
            JsonNode value = objectMapper.readTree(output);
            validateNodeType(schema.path("type").asText(null), value, "output");
            if (value != null && value.isObject()) {
                validateRequired(schema, value.fieldNames()::forEachRemaining);
                if (schema.path("additionalProperties").isBoolean()
                        && !schema.path("additionalProperties").asBoolean()) {
                    Set<String> allowed = propertyNames(schema);
                    value.fieldNames().forEachRemaining(name -> {
                        if (!allowed.contains(name)) throw AiAgentExceptions.invalidExecutionOutput("unknown field " + name);
                    });
                }
            }
        } catch (com.company.scopery.common.exception.AppException exception) {
            throw exception;
        } catch (Exception exception) {
            throw AiAgentExceptions.invalidExecutionOutput("output is not valid JSON for the configured schema");
        }
    }

    private void validateMapAgainstSchema(String schemaJson, Map<String, String> input) {
        try {
            JsonNode schema = objectMapper.readTree(schemaJson);
            Set<String> allowed = propertyNames(schema);
            Set<String> required = new HashSet<>();
            schema.path("required").forEach(node -> required.add(node.asText()));
            Set<String> missing = new HashSet<>(required); missing.removeAll(input.keySet());
            if (!missing.isEmpty()) throw AiAgentExceptions.invalidExecutionInput("missing variables " + missing);
            boolean additionalAllowed = schema.path("additionalProperties").asBoolean(false);
            if (!additionalAllowed) {
                Set<String> unknown = new HashSet<>(input.keySet()); unknown.removeAll(allowed);
                if (!unknown.isEmpty()) throw AiAgentExceptions.invalidExecutionInput("unknown variables " + unknown);
            }
            input.forEach((key, value) -> {
                JsonNode property = schema.path("properties").path(key);
                if (!property.isMissingNode()) validateStringValue(property.path("type").asText("string"), key, value);
            });
        } catch (com.company.scopery.common.exception.AppException exception) {
            throw exception;
        } catch (Exception exception) {
            throw AiAgentExceptions.invalidExecutionInput("configured schema is invalid");
        }
    }

    private Set<String> propertyNames(JsonNode schema) {
        Set<String> names = new HashSet<>();
        schema.path("properties").fieldNames().forEachRemaining(names::add);
        return names;
    }

    private void validateRequired(JsonNode schema, java.util.function.Consumer<java.util.function.Consumer<String>> fields) {
        Set<String> present = new HashSet<>(); fields.accept(present::add);
        schema.path("required").forEach(node -> {
            if (!present.contains(node.asText())) throw AiAgentExceptions.invalidExecutionOutput("missing field " + node.asText());
        });
    }

    private void validateStringValue(String type, String key, String value) {
        try {
            switch (type) {
                case "integer" -> Long.parseLong(value);
                case "number" -> Double.parseDouble(value);
                case "boolean" -> { if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) throw new Exception(); }
                default -> { }
            }
        } catch (Exception exception) {
            throw AiAgentExceptions.invalidExecutionInput("variable " + key + " must be " + type);
        }
    }

    private void validateNodeType(String type, JsonNode value, String field) {
        if (type == null) return;
        boolean valid = switch (type) {
            case "object" -> value != null && value.isObject();
            case "array" -> value != null && value.isArray();
            case "string" -> value != null && value.isTextual();
            case "number" -> value != null && value.isNumber();
            case "integer" -> value != null && value.isIntegralNumber();
            case "boolean" -> value != null && value.isBoolean();
            default -> true;
        };
        if (!valid) throw AiAgentExceptions.invalidExecutionOutput(field + " must be " + type);
    }
}
