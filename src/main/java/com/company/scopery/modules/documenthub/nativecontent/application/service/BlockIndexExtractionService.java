package com.company.scopery.modules.documenthub.nativecontent.application.service;

import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentBlockIndex;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentBlockIndexRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BlockIndexExtractionService {

    private static final Logger log = LoggerFactory.getLogger(BlockIndexExtractionService.class);
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final DocumentBlockIndexRepository blockIndexRepo;
    private final ObjectMapper objectMapper;

    public BlockIndexExtractionService(DocumentBlockIndexRepository blockIndexRepo, ObjectMapper objectMapper) {
        this.blockIndexRepo = blockIndexRepo;
        this.objectMapper = objectMapper;
    }

    public void rebuildIndex(UUID documentId, String ast) {
        List<DocumentBlockIndex> blocks = new ArrayList<>();
        try {
            Map<String, Object> root = objectMapper.readValue(ast, MAP_TYPE);
            walkNode(root, documentId, blocks, new int[]{0});
        } catch (Exception e) {
            log.warn("Block index extraction failed for document {}: {}", documentId, e.getMessage());
            return;
        }
        blockIndexRepo.deleteByDocumentId(documentId);
        if (!blocks.isEmpty()) {
            blockIndexRepo.saveAll(blocks);
        }
    }

    @SuppressWarnings("unchecked")
    private void walkNode(Map<String, Object> node, UUID documentId, List<DocumentBlockIndex> acc, int[] ordinalCounter) {
        if (node == null) return;

        String type = (String) node.get("type");
        String id = (String) node.get("id");

        if (type != null && id != null) {
            String plainText = extractText(node);
            Integer headingLevel = null;
            String headingText = null;

            if (type.startsWith("heading")) {
                try {
                    headingLevel = Integer.parseInt(type.replace("heading", "").replace("Heading", ""));
                } catch (NumberFormatException ignored) {}
                headingText = plainText;
            }

            acc.add(DocumentBlockIndex.create(documentId, id, type, headingLevel, headingText, plainText, ordinalCounter[0]++));
        }

        // Recurse into children
        Object children = node.get("children");
        if (children instanceof List<?> childList) {
            for (Object child : childList) {
                if (child instanceof Map<?, ?> childMap) {
                    walkNode((Map<String, Object>) childMap, documentId, acc, ordinalCounter);
                }
            }
        }

        // Also recurse into content array (some AST formats)
        Object content = node.get("content");
        if (content instanceof List<?> contentList) {
            for (Object item : contentList) {
                if (item instanceof Map<?, ?> itemMap) {
                    walkNode((Map<String, Object>) itemMap, documentId, acc, ordinalCounter);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> node) {
        StringBuilder sb = new StringBuilder();
        Object text = node.get("text");
        if (text instanceof String s) {
            sb.append(s);
        }
        Object children = node.get("children");
        if (children instanceof List<?> childList) {
            for (Object child : childList) {
                if (child instanceof Map<?, ?> childMap) {
                    String childText = extractText((Map<String, Object>) childMap);
                    if (!childText.isBlank()) {
                        if (!sb.isEmpty()) sb.append(" ");
                        sb.append(childText);
                    }
                }
            }
        }
        return sb.toString().trim();
    }
}
