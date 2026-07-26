package com.company.scopery.modules.knowledge.indexing.application.service;

import com.company.scopery.modules.knowledge.indexing.application.response.DocumentIndexStatusResponse;
import com.company.scopery.modules.knowledge.indexing.application.response.IndexJobResponse;
import com.company.scopery.modules.knowledge.indexing.application.response.IndexProjectStatusResponse;
import com.company.scopery.modules.knowledge.indexing.domain.model.KnowledgeIndexJob;
import com.company.scopery.modules.knowledge.indexing.domain.model.KnowledgeIndexJobRepository;
import com.company.scopery.modules.knowledge.indexing.infrastructure.postgres.PostgresKnowledgeIndexService;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeSourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class KnowledgeIndexingQueryService {

    private final KnowledgeIndexJobRepository jobs;
    private final KnowledgeSourceRepository sources;
    private final PostgresKnowledgeIndexService postgresIndexService;

    public KnowledgeIndexingQueryService(KnowledgeIndexJobRepository jobs,
                                          KnowledgeSourceRepository sources,
                                          PostgresKnowledgeIndexService postgresIndexService) {
        this.jobs = jobs;
        this.sources = sources;
        this.postgresIndexService = postgresIndexService;
    }

    @Transactional(readOnly = true)
    public IndexJobResponse findJob(UUID jobId) {
        KnowledgeIndexJob job = jobs.findById(jobId)
                .orElseThrow(() -> KnowledgeExceptions.knowledgeIndexJobNotFound(jobId));
        return toResponse(job);
    }

    @Transactional(readOnly = true)
    public IndexProjectStatusResponse getProjectStatus(UUID projectId) {
        int indexedSources = sources.findByProjectId(projectId).size();
        int total = postgresIndexService.countTotalChunksByProjectId(projectId);
        int embedded = postgresIndexService.countEmbeddedChunksByProjectId(projectId);
        return new IndexProjectStatusResponse(projectId, indexedSources, total, embedded, total - embedded, total > 0 && embedded == total);
    }

    @Transactional(readOnly = true)
    public DocumentIndexStatusResponse getDocumentStatus(UUID projectId, UUID documentId) {
        PostgresKnowledgeIndexService.DocumentIndexStats stats =
                postgresIndexService.getDocumentStats(documentId);
        return new DocumentIndexStatusResponse(
                documentId, projectId,
                stats.totalChunks() > 0 && stats.embeddedChunks() == stats.totalChunks(),
                stats.totalChunks(), stats.embeddedChunks(),
                stats.lastIndexedAt());
    }

    private IndexJobResponse toResponse(KnowledgeIndexJob job) {
        return new IndexJobResponse(job.id(), job.workspaceId(), job.projectId(), job.sourceId(),
                job.jobType().name(), job.jobStatus().name(), job.idempotencyKey(),
                job.processedCount(), job.successCount(), job.failureCount(), job.queuedAt());
    }
}
