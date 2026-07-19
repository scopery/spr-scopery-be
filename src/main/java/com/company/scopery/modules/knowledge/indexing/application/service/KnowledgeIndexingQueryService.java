package com.company.scopery.modules.knowledge.indexing.application.service;

import com.company.scopery.modules.knowledge.indexing.application.response.IndexJobResponse;
import com.company.scopery.modules.knowledge.indexing.domain.model.KnowledgeIndexJob;
import com.company.scopery.modules.knowledge.indexing.domain.model.KnowledgeIndexJobRepository;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class KnowledgeIndexingQueryService {

    private final KnowledgeIndexJobRepository jobs;

    public KnowledgeIndexingQueryService(KnowledgeIndexJobRepository jobs) {
        this.jobs = jobs;
    }

    @Transactional(readOnly = true)
    public IndexJobResponse findJob(UUID jobId) {
        KnowledgeIndexJob job = jobs.findById(jobId)
                .orElseThrow(() -> KnowledgeExceptions.knowledgeIndexJobNotFound(jobId));
        return toResponse(job);
    }

    private IndexJobResponse toResponse(KnowledgeIndexJob job) {
        return new IndexJobResponse(job.id(), job.workspaceId(), job.projectId(), job.sourceId(),
                job.jobType().name(), job.jobStatus().name(), job.idempotencyKey(),
                job.processedCount(), job.successCount(), job.failureCount(), job.queuedAt());
    }
}
