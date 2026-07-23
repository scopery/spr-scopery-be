package com.company.scopery.modules.documenthub.nativecontent.application.action;

import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.documenthub.document.domain.model.Document;
import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.nativecontent.application.command.SaveDocumentContentCommand;
import com.company.scopery.modules.documenthub.nativecontent.application.response.DocumentContentResponse;
import com.company.scopery.modules.documenthub.nativecontent.domain.enums.RevisionType;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentContent;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentContentRepository;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentRevision;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentRevisionRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubOutboxEventCodes;
import com.company.scopery.modules.documenthub.nativecontent.application.service.BlockIndexExtractionService;
import com.company.scopery.modules.documenthub.nativecontent.application.service.MentionExtractionService;
import com.company.scopery.modules.documenthub.nativecontent.application.service.RelationExtractionService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

@Component
public class SaveDocumentContentAction {

    private final DocumentRepository documents;
    private final DocumentContentRepository contentRepo;
    private final DocumentRevisionRepository revisionRepo;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;
    private final TransactionalOutboxService outbox;
    private final BlockIndexExtractionService blockIndexExtraction;
    private final MentionExtractionService mentionExtraction;
    private final RelationExtractionService relationExtraction;

    public SaveDocumentContentAction(DocumentRepository documents,
                                      DocumentContentRepository contentRepo,
                                      DocumentRevisionRepository revisionRepo,
                                      DocumentHubAuthorizationService authorization,
                                      DocumentHubActivityLogger activityLogger,
                                      TransactionalOutboxService outbox,
                                      BlockIndexExtractionService blockIndexExtraction,
                                      MentionExtractionService mentionExtraction,
                                      RelationExtractionService relationExtraction) {
        this.documents = documents;
        this.contentRepo = contentRepo;
        this.revisionRepo = revisionRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
        this.outbox = outbox;
        this.blockIndexExtraction = blockIndexExtraction;
        this.mentionExtraction = mentionExtraction;
        this.relationExtraction = relationExtraction;
    }

    @Transactional
    public DocumentContentResponse execute(SaveDocumentContentCommand c) {
        authorization.requireUpdate(c.projectId());

        Document document = documents.findByIdAndProjectId(c.documentId(), c.projectId())
                .orElseThrow(() -> DocumentHubExceptions.documentNotFound(c.documentId()));

        if (document.isArchived()) {
            throw DocumentHubExceptions.documentArchivedForEdit(c.documentId());
        }
        if (document.locked()) {
            throw DocumentHubExceptions.documentLockedForEdit(c.documentId());
        }
        if (!document.isNativeEditable()) {
            throw DocumentHubExceptions.contentNotSupported(c.documentId());
        }

        String incomingChecksum = sha256(c.ast());

        // No-op detection: skip save if content is identical
        var existing = contentRepo.findByDocumentId(c.documentId());
        if (existing.isPresent() && incomingChecksum.equals(existing.get().checksum())) {
            activityLogger.logSuccess(DocumentHubEntityTypes.CONTENT, c.documentId(),
                    DocumentHubActivityActions.CONTENT_NOOP_DETECTED, "Content save no-op");
            return DocumentContentResponse.from(existing.get());
        }

        // Optimistic lock: expected base revision must match current
        long currentRevisionNo = document.currentContentRevisionNo();
        if (existing.isPresent() && c.expectedBaseRevisionNo() != currentRevisionNo) {
            throw DocumentHubExceptions.contentRevisionConflict(c.expectedBaseRevisionNo(), currentRevisionNo);
        }

        String plainText = extractPlainText(c.ast());
        int wordCount = countWords(plainText);
        int characterCount = plainText.length();
        long newRevisionNo = currentRevisionNo + 1;
        RevisionType revisionType = c.revisionType() != null ? c.revisionType() : RevisionType.MANUAL;
        UUID actorId = resolveActorId();

        // Save immutable revision
        DocumentRevision revision = DocumentRevision.create(c.documentId(), document.workspaceId(),
                c.projectId(), newRevisionNo, revisionType, c.ast(), plainText,
                incomingChecksum, c.schemaVersion(), wordCount, characterCount, actorId);
        DocumentRevision savedRevision = revisionRepo.save(revision);

        // Upsert content
        DocumentContent updatedContent;
        if (existing.isPresent()) {
            updatedContent = contentRepo.save(existing.get().withUpdatedContent(
                    newRevisionNo, c.ast(), plainText, wordCount, characterCount,
                    incomingChecksum, c.schemaVersion(), actorId));
        } else {
            updatedContent = contentRepo.save(DocumentContent.create(c.documentId(), document.workspaceId(),
                    c.projectId(), c.schemaVersion(), newRevisionNo, c.ast(), plainText,
                    wordCount, characterCount, incomingChecksum, actorId));
        }

        // Update document record
        Document updatedDocument = document.withNativeContentSaved(
                savedRevision.id(), newRevisionNo, incomingChecksum, c.schemaVersion(), actorId);
        documents.save(updatedDocument);

        // Rebuild block index, mention, and relation projections
        blockIndexExtraction.rebuildIndex(c.documentId(), c.ast());
        mentionExtraction.rebuildMentions(c.documentId(), document.workspaceId(), c.projectId(), c.ast());
        relationExtraction.rebuildRelations(c.documentId(), c.ast());

        outbox.enqueue(DocumentHubEntityTypes.CONTENT, c.documentId(),
                DocumentHubOutboxEventCodes.DOCUMENT_CONTENT_SAVED,
                Map.of("documentId", c.documentId(), "revisionNo", newRevisionNo,
                        "projectId", c.projectId(), "checksum", incomingChecksum));

        activityLogger.logSuccess(DocumentHubEntityTypes.CONTENT, c.documentId(),
                DocumentHubActivityActions.CONTENT_SAVED, "Content saved, revision " + newRevisionNo);

        return DocumentContentResponse.from(updatedContent);
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private String extractPlainText(String ast) {
        if (ast == null || ast.isBlank()) return "";
        try {
            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            var root = mapper.readTree(ast);
            var sb = new StringBuilder();
            collectText(root, sb);
            return sb.toString().replaceAll("\\s+", " ").trim();
        } catch (Exception e) {
            // fallback: strip JSON punctuation
            return ast.replaceAll("\"[^\"]*\"\\s*:\\s*[^,}\\]]+", "")
                      .replaceAll("[\"\\[\\]{}:,]", " ")
                      .replaceAll("\\s+", " ")
                      .trim();
        }
    }

    private void collectText(com.fasterxml.jackson.databind.JsonNode node, StringBuilder sb) {
        if (node.isTextual()) {
            String val = node.asText().trim();
            if (!val.isEmpty()) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(val);
            }
            return;
        }
        if (node.isObject()) {
            // Only collect value of "text" key — skip metadata keys like "type", "id", "attrs"
            com.fasterxml.jackson.databind.JsonNode textNode = node.get("text");
            if (textNode != null && textNode.isTextual()) {
                String val = textNode.asText().trim();
                if (!val.isEmpty()) {
                    if (sb.length() > 0) sb.append(" ");
                    sb.append(val);
                }
            } else {
                com.fasterxml.jackson.databind.JsonNode content = node.get("content");
                if (content != null) collectText(content, sb);
                com.fasterxml.jackson.databind.JsonNode children = node.get("children");
                if (children != null) collectText(children, sb);
            }
            return;
        }
        if (node.isArray()) {
            for (com.fasterxml.jackson.databind.JsonNode child : node) {
                collectText(child, sb);
            }
        }
    }

    private int countWords(String plainText) {
        if (plainText == null || plainText.isBlank()) return 0;
        return plainText.trim().split("\\s+").length;
    }

    private UUID resolveActorId() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof String userId) {
                return UUID.fromString(userId);
            }
        } catch (Exception ignored) {}
        return null;
    }
}
