package com.company.scopery.modules.knowledge.indexing.application.action;

import com.company.scopery.modules.documenthub.document.domain.model.Document;
import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.knowledge.indexing.application.command.ReindexProjectCommand;
import com.company.scopery.modules.knowledge.indexing.application.response.IndexJobResponse;
import com.company.scopery.modules.knowledge.indexing.application.service.KnowledgeSourceIndexingService;
import com.company.scopery.modules.knowledge.indexing.domain.enums.IndexJobStatus;
import com.company.scopery.modules.knowledge.indexing.domain.enums.IndexJobType;
import com.company.scopery.modules.knowledge.indexing.domain.model.KnowledgeIndexJob;
import com.company.scopery.modules.knowledge.indexing.domain.model.KnowledgeIndexJobRepository;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeSource;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeSourceRepository;
import com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter.FunctionalItemKnowledgeSourceAdapter;
import com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter.NativeDocumentContentSourceAdapter;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItem;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ReindexProjectAction {

    private static final Logger log = LoggerFactory.getLogger(ReindexProjectAction.class);

    private final KnowledgeSourceRepository sources;
    private final KnowledgeIndexJobRepository jobs;
    private final KnowledgeSourceIndexingService indexingService;
    private final DocumentRepository documentRepo;
    private final NativeDocumentContentSourceAdapter documentAdapter;
    private final FunctionalItemRepository functionalItemRepo;
    private final FunctionalItemKnowledgeSourceAdapter functionalItemAdapter;

    public ReindexProjectAction(KnowledgeSourceRepository sources,
                                 KnowledgeIndexJobRepository jobs,
                                 KnowledgeSourceIndexingService indexingService,
                                 DocumentRepository documentRepo,
                                 NativeDocumentContentSourceAdapter documentAdapter,
                                 FunctionalItemRepository functionalItemRepo,
                                 FunctionalItemKnowledgeSourceAdapter functionalItemAdapter) {
        this.sources = sources;
        this.jobs = jobs;
        this.indexingService = indexingService;
        this.documentRepo = documentRepo;
        this.documentAdapter = documentAdapter;
        this.functionalItemRepo = functionalItemRepo;
        this.functionalItemAdapter = functionalItemAdapter;
    }

    @Transactional
    public IndexJobResponse execute(ReindexProjectCommand command) {
        // Unique per run — avoids unique-constraint collision when reindex is called multiple times
        String idempotencyKey = "reindex-project:" + command.projectId() + ":" + Instant.now().toEpochMilli();

        Instant now = Instant.now();
        KnowledgeIndexJob job = new KnowledgeIndexJob(UUID.randomUUID(),
                command.workspaceId(), command.projectId(), null, null,
                IndexJobType.PROJECT_REINDEX, IndexJobStatus.RUNNING,
                idempotencyKey, null, 0, 0, 0, 0, null, null, now, now, null, command.requestedByActorId());
        job = jobs.save(job);

        // Invalidate stale sources so orphaned entries (deleted docs/FRs) are cleaned up
        List<KnowledgeSource> projectSources = sources.findByProjectId(command.projectId());
        for (KnowledgeSource s : projectSources) {
            try {
                indexingService.invalidateSource(s.workspaceId(), s.sourceRefId());
            } catch (Exception e) {
                log.warn("ReindexProject: failed to invalidate source {}: {}", s.id(), e.getMessage());
            }
        }

        int processed = 0, success = 0, failure = 0;

        // Re-index all project documents from their current content
        List<Document> documents = documentRepo.findByProjectId(command.projectId());
        for (Document doc : documents) {
            processed++;
            try {
                var snapshot = documentAdapter.buildSnapshot(command.projectId(), doc.id());
                if (snapshot.isPresent()) {
                    indexingService.upsertSource(snapshot.get());
                    success++;
                }
            } catch (Exception e) {
                log.warn("ReindexProject: failed to index document {}: {}", doc.id(), e.getMessage());
                failure++;
            }
        }

        // Re-index all functional items
        List<FunctionalItem> functionalItems = functionalItemRepo.findByProjectId(command.projectId());
        for (FunctionalItem item : functionalItems) {
            processed++;
            try {
                var snapshot = functionalItemAdapter.buildSnapshot(command.projectId(), item.id());
                if (snapshot.isPresent()) {
                    indexingService.upsertSource(snapshot.get());
                    success++;
                }
            } catch (Exception e) {
                log.warn("ReindexProject: failed to index functional item {}: {}", item.id(), e.getMessage());
                failure++;
            }
        }

        Instant completedAt = Instant.now();
        job = new KnowledgeIndexJob(job.id(), job.workspaceId(), job.projectId(), null, null,
                IndexJobType.PROJECT_REINDEX, IndexJobStatus.SUCCEEDED,
                idempotencyKey, null, 1, processed, success, failure,
                null, null, now, now, completedAt, command.requestedByActorId());
        return toResponse(jobs.save(job));
    }

    private IndexJobResponse toResponse(KnowledgeIndexJob job) {
        return new IndexJobResponse(job.id(), job.workspaceId(), job.projectId(), job.sourceId(),
                job.jobType().name(), job.jobStatus().name(), job.idempotencyKey(),
                job.processedCount(), job.successCount(), job.failureCount(), job.queuedAt());
    }
}
