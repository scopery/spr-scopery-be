package com.company.scopery.modules.documenthub.nativecontent.application.service;

import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentMention;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentMentionRepository;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubOutboxEventCodes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MentionExtractionService {

    private static final Logger log = LoggerFactory.getLogger(MentionExtractionService.class);
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final DocumentMentionRepository mentionRepo;
    private final TransactionalOutboxService outbox;
    private final ObjectMapper objectMapper;

    public MentionExtractionService(DocumentMentionRepository mentionRepo,
                                     TransactionalOutboxService outbox,
                                     ObjectMapper objectMapper) {
        this.mentionRepo = mentionRepo;
        this.outbox = outbox;
        this.objectMapper = objectMapper;
    }

    public void rebuildMentions(UUID documentId, UUID workspaceId, UUID projectId, String ast) {
        List<DocumentMention> extracted = new ArrayList<>();
        try {
            Map<String, Object> root = objectMapper.readValue(ast, MAP_TYPE);
            walkForMentions(root, documentId, workspaceId, projectId, extracted);
        } catch (Exception e) {
            log.warn("Mention extraction failed for document {}: {}", documentId, e.getMessage());
            return;
        }

        mentionRepo.deleteByDocumentId(documentId);
        if (!extracted.isEmpty()) {
            mentionRepo.saveAll(extracted);
            // Emit outbox for USER/TEAM mentions so notification layer can act
            boolean hasUserOrTeamMention = extracted.stream()
                    .anyMatch(m -> "USER".equals(m.mentionedResourceType()) || "TEAM".equals(m.mentionedResourceType()));
            if (hasUserOrTeamMention) {
                outbox.enqueue("DOCUMENT", documentId, DocumentHubOutboxEventCodes.DOCUMENT_MENTION_EXTRACTED,
                        Map.of("documentId", documentId, "projectId", projectId, "mentionCount", extracted.size()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void walkForMentions(Map<String, Object> node, UUID documentId, UUID workspaceId, UUID projectId,
                                  List<DocumentMention> acc) {
        if (node == null) return;
        String type = (String) node.get("type");
        if ("resourceMention".equals(type)) {
            String mentionType = (String) node.getOrDefault("mentionType", "INLINE");
            String resourceType = (String) node.get("mentionedResourceType");
            String resourceIdStr = (String) node.get("mentionedResourceId");
            String blockId = (String) node.get("blockId");
            if (resourceType != null && resourceIdStr != null) {
                try {
                    UUID resourceId = UUID.fromString(resourceIdStr);
                    acc.add(DocumentMention.create(documentId, workspaceId, projectId,
                            blockId, mentionType, resourceType, resourceId));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        for (String key : List.of("children", "content")) {
            Object children = node.get(key);
            if (children instanceof List<?> list) {
                for (Object child : list) {
                    if (child instanceof Map<?, ?> childMap) {
                        walkForMentions((Map<String, Object>) childMap, documentId, workspaceId, projectId, acc);
                    }
                }
            }
        }
    }
}
