package com.company.scopery.modules.specpack.blockimport.application.service;

import com.company.scopery.modules.specpack.blockimport.domain.model.BlockImportItem;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class SpecPackBlockJsonParser {

    private static final String EXPECTED_SCHEMA_VERSION = "spec-pack-block-import-v1";
    private static final Set<String> FORBIDDEN_KEYS = Set.of("__proto__", "constructor", "prototype");
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};
    private static final TypeReference<List<Map<String, Object>>> LIST_MAP_TYPE = new TypeReference<>() {};

    @Value("${specpack.import.max-json-bytes:10485760}")
    private long maxJsonBytes;

    @Value("${specpack.import.max-items-per-import:500}")
    private int maxItems;

    private final ObjectMapper objectMapper;

    public SpecPackBlockJsonParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    public ParseResult parse(byte[] payload) {
        if (payload.length > maxJsonBytes) {
            throw SpecPackExceptions.importPayloadTooLarge(payload.length, maxJsonBytes);
        }

        Map<String, Object> root;
        try {
            root = objectMapper.readValue(payload, MAP_TYPE);
        } catch (Exception e) {
            throw SpecPackExceptions.importInvalidBlock(null, "Invalid JSON: " + e.getMessage());
        }

        rejectForbiddenKeys(root, "$");

        String schemaVersion = (String) root.get("schemaVersion");
        if (!EXPECTED_SCHEMA_VERSION.equals(schemaVersion)) {
            throw SpecPackExceptions.importInvalidSchemaVersion(schemaVersion);
        }

        Object blocksRaw = root.get("blocks");
        if (!(blocksRaw instanceof List<?> blocksList)) {
            throw SpecPackExceptions.importInvalidBlock(null, "Missing or invalid 'blocks' array");
        }

        if (blocksList.size() > maxItems) {
            throw SpecPackExceptions.importTooManyItems(blocksList.size(), maxItems);
        }

        List<BlockImportItem> items = new ArrayList<>();
        for (Object raw : blocksList) {
            if (!(raw instanceof Map<?, ?> rawMap)) continue;
            Map<String, Object> entry = (Map<String, Object>) rawMap;
            rejectForbiddenKeys(entry, "$.blocks[]");
            items.add(parseItem(entry));
        }

        return new ParseResult(schemaVersion, items);
    }

    @SuppressWarnings("unchecked")
    private BlockImportItem parseItem(Map<String, Object> entry) {
        String blockKey = getString(entry, "blockKey");
        String blockType = getString(entry, "blockType");
        String title = getString(entry, "title");
        String parentBlockKey = getString(entry, "parentBlockKey");
        int displayOrder = entry.get("displayOrder") instanceof Number n ? n.intValue() : 0;
        String contentFormat = getString(entry, "contentFormat");
        Map<String, Object> contentJson = entry.get("contentJson") instanceof Map<?, ?> m
                ? (Map<String, Object>) m : Map.of();
        List<Map<String, Object>> sourceRefsJson = entry.get("sourceRefsJson") instanceof List<?> l
                ? (List<Map<String, Object>>) l : null;
        Map<String, Object> selfCheck = entry.get("selfCheck") instanceof Map<?, ?> sc
                ? (Map<String, Object>) sc : null;

        return new BlockImportItem(blockKey, blockType, title, parentBlockKey,
                displayOrder, contentFormat, contentJson, sourceRefsJson, selfCheck, List.of(), null);
    }

    private void rejectForbiddenKeys(Map<String, Object> map, String path) {
        for (String key : map.keySet()) {
            if (FORBIDDEN_KEYS.contains(key)) {
                throw SpecPackExceptions.importInvalidBlock(null,
                        "Forbidden key '" + key + "' at " + path);
            }
        }
    }

    private String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val instanceof String s ? s : null;
    }

    public record ParseResult(String schemaVersion, List<BlockImportItem> items) {}
}
