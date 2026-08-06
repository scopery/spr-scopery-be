package com.company.scopery.modules.specpack.blockimport.application.service;

import com.company.scopery.modules.specpack.block.domain.enums.BlockType;
import com.company.scopery.modules.specpack.block.domain.enums.ContentFormat;
import com.company.scopery.modules.specpack.blockimport.domain.model.BlockImportItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class SpecPackBlockSchemaValidator {

    private static final Set<ContentFormat> DIAGRAM_FORMATS = Set.of(
            ContentFormat.MERMAID, ContentFormat.PLANTUML, ContentFormat.DRAWIO_XML,
            ContentFormat.BPMN_XML, ContentFormat.GRAPHVIZ_DOT
    );

    public List<BlockImportItem> validate(List<BlockImportItem> items) {
        Set<String> seenKeys = new HashSet<>();
        List<BlockImportItem> result = new ArrayList<>();

        for (BlockImportItem item : items) {
            List<String> errors = validateItem(item, seenKeys);
            result.add(errors.isEmpty() ? item : item.withErrors(errors));
            if (errors.isEmpty() && item.blockKey() != null) {
                seenKeys.add(item.blockKey());
            }
        }
        return result;
    }

    private List<String> validateItem(BlockImportItem item, Set<String> seenKeys) {
        List<String> errors = new ArrayList<>();

        if (item.blockKey() == null || item.blockKey().isBlank()) {
            errors.add("blockKey is required");
        } else if (item.blockKey().contains(" ")) {
            errors.add("blockKey must not contain spaces");
        } else if (seenKeys.contains(item.blockKey())) {
            errors.add("duplicate blockKey: " + item.blockKey());
        }

        if (item.blockType() == null || item.blockType().isBlank()) {
            errors.add("blockType is required");
            return errors;
        }

        BlockType blockType;
        try {
            blockType = BlockType.valueOf(item.blockType());
        } catch (IllegalArgumentException e) {
            errors.add("unknown blockType: " + item.blockType());
            return errors;
        }

        if (item.contentFormat() == null || item.contentFormat().isBlank()) {
            errors.add("contentFormat is required");
            return errors;
        }

        ContentFormat contentFormat;
        try {
            contentFormat = ContentFormat.valueOf(item.contentFormat());
        } catch (IllegalArgumentException e) {
            errors.add("unknown contentFormat: " + item.contentFormat());
            return errors;
        }

        switch (blockType) {
            case SECTION -> {
                if (item.title() == null || item.title().isBlank())
                    errors.add("SECTION requires title");
            }
            case IMAGE -> {
                if (contentFormat != ContentFormat.ASSET_REFERENCE)
                    errors.add("IMAGE block must use ASSET_REFERENCE contentFormat");
                if (getStringField(item.contentJson(), "altText") == null)
                    errors.add("IMAGE block requires contentJson.altText");
            }
            case DIAGRAM -> {
                if (!DIAGRAM_FORMATS.contains(contentFormat))
                    errors.add("DIAGRAM block must use a diagram contentFormat (MERMAID, PLANTUML, etc.)");
                if (getStringField(item.contentJson(), "altText") == null)
                    errors.add("DIAGRAM block requires contentJson.altText");
            }
            case TABLE -> {
                Object cols = item.contentJson() != null ? item.contentJson().get("columns") : null;
                if (cols instanceof List<?> colList && colList.size() > 100)
                    errors.add("TABLE columns exceed maximum of 100");
            }
            case PAGE_BREAK -> {
                if (item.parentBlockKey() != null)
                    errors.add("PAGE_BREAK cannot have a parent block");
            }
            default -> { /* no additional rules */ }
        }

        if (item.sourceRefsJson() != null) {
            for (Map<String, Object> ref : item.sourceRefsJson()) {
                String refType = getStringField(ref, "type");
                if ("CUSTOM".equals(refType) && getStringField(ref, "title") == null) {
                    errors.add("CUSTOM sourceRef requires title");
                }
            }
        }

        return errors;
    }

    private String getStringField(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object val = map.get(key);
        return val instanceof String s && !s.isBlank() ? s : null;
    }
}
