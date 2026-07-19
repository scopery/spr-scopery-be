package com.company.scopery.modules.reporting.shared.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Flattens report snapshot JSON into a single-sheet XLSX (key/value rows), parallel to {@link CsvReportSerializer}.
 * Returns Base64-encoded workbook bytes for storage in export job content text.
 */
public final class XlsxReportSerializer {
    private final ObjectMapper objectMapper;

    public XlsxReportSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toXlsxBase64(String dataJson) {
        Map<String, String> flat = flatten(parse(dataJson));
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Report");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("key");
            header.createCell(1).setCellValue("value");
            int rowIdx = 1;
            for (Map.Entry<String, String> entry : flat.entrySet()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }
            workbook.write(out);
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to serialize XLSX report export", ex);
        }
    }

    private Map<String, String> flatten(Map<String, Object> root) {
        Map<String, String> flat = new LinkedHashMap<>();
        flatten("", root, flat);
        return flat;
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
}
