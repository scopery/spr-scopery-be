package com.company.scopery.modules.reporting.shared.export;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Flattens report snapshot JSON into a usable CSV with header + one row (or multiple for list fields).
 */
public final class CsvReportSerializer {
    private final ObjectMapper objectMapper;

    public CsvReportSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toCsv(String dataJson) {
        Map<String, Object> root = parse(dataJson);
        if (root.isEmpty()) {
            return "key,value\n";
        }
        Map<String, String> flat = new LinkedHashMap<>();
        flatten("", root, flat);
        StringBuilder sb = new StringBuilder();
        sb.append(escape("key")).append(',').append(escape("value")).append('\n');
        for (Map.Entry<String, String> e : flat.entrySet()) {
            sb.append(escape(e.getKey())).append(',').append(escape(e.getValue())).append('\n');
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private void flatten(String prefix, Object value, Map<String, String> out) {
        if (value == null) {
            out.put(prefix.isEmpty() ? "value" : prefix, "");
            return;
        }
        if (value instanceof Map<?, ?> map) {
            if (map.isEmpty()) {
                out.put(prefix.isEmpty() ? "value" : prefix, "{}");
                return;
            }
            for (Map.Entry<?, ?> e : map.entrySet()) {
                String key = String.valueOf(e.getKey());
                String next = prefix.isEmpty() ? key : prefix + "." + key;
                flatten(next, e.getValue(), out);
            }
            return;
        }
        if (value instanceof Collection<?> col) {
            if (col.isEmpty()) {
                out.put(prefix, "[]");
                return;
            }
            int i = 0;
            for (Object item : col) {
                flatten(prefix + "[" + i + "]", item, out);
                i++;
            }
            return;
        }
        out.put(prefix.isEmpty() ? "value" : prefix, String.valueOf(value));
    }

    private Map<String, Object> parse(String dataJson) {
        if (dataJson == null || dataJson.isBlank()) {
            return Map.of();
        }
        try {
            Object parsed = objectMapper.readValue(dataJson, Object.class);
            if (parsed instanceof Map<?, ?> map) {
                Map<String, Object> result = new LinkedHashMap<>();
                for (Map.Entry<?, ?> e : map.entrySet()) {
                    result.put(String.valueOf(e.getKey()), e.getValue());
                }
                return result;
            }
            if (parsed instanceof List<?> list) {
                return Map.of("items", new ArrayList<>(list));
            }
            return Map.of("value", parsed);
        } catch (Exception ex) {
            return Map.of("raw", dataJson);
        }
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        boolean needsQuotes = value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r");
        String escaped = value.replace("\"", "\"\"");
        return needsQuotes ? "\"" + escaped + "\"" : escaped;
    }
}
