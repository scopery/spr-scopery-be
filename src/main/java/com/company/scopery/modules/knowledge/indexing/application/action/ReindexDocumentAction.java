package com.company.scopery.modules.knowledge.indexing.application.action;

import com.company.scopery.modules.knowledge.indexing.application.response.DocumentIndexStatusResponse;
import com.company.scopery.modules.knowledge.indexing.application.service.KnowledgeSourceIndexingService;
import com.company.scopery.modules.knowledge.indexing.infrastructure.postgres.PostgresKnowledgeIndexService;
import com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter.NativeDocumentContentSourceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ReindexDocumentAction {

    private static final Logger log = LoggerFactory.getLogger(ReindexDocumentAction.class);

    private final NativeDocumentContentSourceAdapter documentAdapter;
    private final KnowledgeSourceIndexingService indexingService;
    private final PostgresKnowledgeIndexService postgresIndexService;

    public ReindexDocumentAction(NativeDocumentContentSourceAdapter documentAdapter,
                                  KnowledgeSourceIndexingService indexingService,
                                  PostgresKnowledgeIndexService postgresIndexService) {
        this.documentAdapter = documentAdapter;
        this.indexingService = indexingService;
        this.postgresIndexService = postgresIndexService;
    }

    @Transactional
    public DocumentIndexStatusResponse execute(UUID projectId, UUID documentId) {
        documentAdapter.buildSnapshot(projectId, documentId).ifPresent(snapshot -> {
            indexingService.upsertSource(snapshot);
            log.info("[ReindexDocument] Indexed document={} project={}", documentId, projectId);
        });
        return buildStatus(projectId, documentId);
    }

    private DocumentIndexStatusResponse buildStatus(UUID projectId, UUID documentId) {
        PostgresKnowledgeIndexService.DocumentIndexStats stats =
                postgresIndexService.getDocumentStats(documentId);
        return new DocumentIndexStatusResponse(
                documentId, projectId,
                stats.totalChunks() > 0 && stats.embeddedChunks() == stats.totalChunks(),
                stats.totalChunks(), stats.embeddedChunks(),
                stats.lastIndexedAt());
    }
}
