package com.company.scopery.modules.knowledge.indexing.application.action;

import com.company.scopery.modules.knowledge.indexing.application.command.ReindexWorkspaceCommand;
import com.company.scopery.modules.knowledge.indexing.application.response.IndexJobResponse;
import com.company.scopery.modules.knowledge.indexing.application.service.KnowledgeSourceIndexingService;
import com.company.scopery.modules.knowledge.indexing.domain.enums.IndexJobStatus;
import com.company.scopery.modules.knowledge.indexing.domain.enums.IndexJobType;
import com.company.scopery.modules.knowledge.indexing.domain.model.KnowledgeIndexJob;
import com.company.scopery.modules.knowledge.indexing.domain.model.KnowledgeIndexJobRepository;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeSource;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeSourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ReindexWorkspaceAction {

    private static final Logger log = LoggerFactory.getLogger(ReindexWorkspaceAction.class);

    private final KnowledgeSourceRepository sources;
    private final KnowledgeIndexJobRepository jobs;
    private final KnowledgeSourceIndexingService indexingService;

    public ReindexWorkspaceAction(KnowledgeSourceRepository sources,
                                   KnowledgeIndexJobRepository jobs,
                                   KnowledgeSourceIndexingService indexingService) {
        this.sources = sources;
        this.jobs = jobs;
        this.indexingService = indexingService;
    }

    @Transactional
    public IndexJobResponse execute(ReindexWorkspaceCommand command) {
        String idempotencyKey = "reindex-workspace:" + command.workspaceId() + ":v1";
        Instant now = Instant.now();

        KnowledgeIndexJob job = new KnowledgeIndexJob(UUID.randomUUID(),
                command.workspaceId(), null, null, null,
                IndexJobType.PROJECT_REINDEX, IndexJobStatus.RUNNING,
                idempotencyKey, null, 0, 0, 0, 0, null, null, now, now, null, command.requestedByActorId());
        job = jobs.save(job);

        List<KnowledgeSource> workspaceSources = sources.findByWorkspaceId(command.workspaceId());
        int processed = 0, success = 0, failure = 0;
        for (KnowledgeSource s : workspaceSources) {
            processed++;
            try {
                indexingService.invalidateSource(s.workspaceId(), s.sourceRefId());
                success++;
            } catch (Exception e) {
                log.warn("Failed to invalidate source {}: {}", s.id(), e.getMessage());
                failure++;
            }
        }

        final int fp = processed, fs = success, ff = failure;
        Instant completedAt = Instant.now();
        job = new KnowledgeIndexJob(job.id(), job.workspaceId(), null, null, null,
                IndexJobType.PROJECT_REINDEX, IndexJobStatus.SUCCEEDED,
                idempotencyKey, null, 1, fp, fs, ff, null, null, now, now, completedAt, command.requestedByActorId());
        return toResponse(jobs.save(job));
    }

    private IndexJobResponse toResponse(KnowledgeIndexJob job) {
        return new IndexJobResponse(job.id(), job.workspaceId(), job.projectId(), job.sourceId(),
                job.jobType().name(), job.jobStatus().name(), job.idempotencyKey(),
                job.processedCount(), job.successCount(), job.failureCount(), job.queuedAt());
    }
}
