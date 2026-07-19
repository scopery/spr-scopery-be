package com.company.scopery.modules.documenthub.nativecontent.application.service;

import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentRelation;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentRelationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RelationExtractionService {

    private static final Logger log = LoggerFactory.getLogger(RelationExtractionService.class);
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final DocumentRelationRepository relationRepo;
    private final ObjectMapper objectMapper;

    public RelationExtractionService(DocumentRelationRepository relationRepo, ObjectMapper objectMapper) {
        this.relationRepo = relationRepo;
        this.objectMapper = objectMapper;
    }

    public void rebuildRelations(UUID sourceDocumentId, String ast) {
        List<DocumentRelation> extracted = new ArrayList<>();
        try {
            Map<String, Object> root = objectMapper.readValue(ast, MAP_TYPE);
            walkForRelations(root, sourceDocumentId, extracted);
        } catch (Exception e) {
            log.warn("Relation extraction failed for document {}: {}", sourceDocumentId, e.getMessage());
            return;
        }

        relationRepo.deleteBySourceDocumentId(sourceDocumentId);
        if (!extracted.isEmpty()) {
            relationRepo.saveAll(extracted);
        }
    }

    @SuppressWarnings("unchecked")
    private void walkForRelations(Map<String, Object> node, UUID sourceDocumentId, List<DocumentRelation> acc) {
        if (node == null) return;
        String type = (String) node.get("type");
        if ("documentLink".equals(type) || "inlineLink".equals(type)) {
            String targetIdStr = (String) node.get("targetDocumentId");
            String blockId = (String) node.get("blockId");
            String relationType = (String) node.getOrDefault("relationType", "LINK");
            if (targetIdStr != null) {
                try {
                    UUID targetId = UUID.fromString(targetIdStr);
                    if (!targetId.equals(sourceDocumentId)) {
                        acc.add(DocumentRelation.create(sourceDocumentId, targetId, relationType, blockId));
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        }
        for (String key : List.of("children", "content")) {
            Object children = node.get(key);
            if (children instanceof List<?> list) {
                for (Object child : list) {
                    if (child instanceof Map<?, ?> childMap) {
                        walkForRelations((Map<String, Object>) childMap, sourceDocumentId, acc);
                    }
                }
            }
        }
    }
}
