package com.company.scopery.modules.knowledge.source.application.action;

import com.company.scopery.modules.knowledge.indexing.application.response.IndexJobResponse;
import com.company.scopery.modules.knowledge.indexing.application.service.KnowledgeSourceIndexingService;
import com.company.scopery.modules.knowledge.indexing.domain.enums.IndexJobStatus;
import com.company.scopery.modules.knowledge.indexing.domain.enums.IndexJobType;
import com.company.scopery.modules.knowledge.indexing.domain.model.KnowledgeIndexJob;
import com.company.scopery.modules.knowledge.indexing.domain.model.KnowledgeIndexJobRepository;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import com.company.scopery.modules.knowledge.source.application.command.ReindexSourceCommand;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeSource;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeSourceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
public class ReindexSourceAction {

    private final KnowledgeSourceRepository sources;
    private final KnowledgeIndexJobRepository jobs;
    private final KnowledgeSourceIndexingService indexingService;

    public ReindexSourceAction(KnowledgeSourceRepository sources,
                                KnowledgeIndexJobRepository jobs,
                                KnowledgeSourceIndexingService indexingService) {
        this.sources = sources;
        this.jobs = jobs;
        this.indexingService = indexingService;
    }

    @Transactional
    public IndexJobResponse execute(ReindexSourceCommand command) {
        KnowledgeSource source = sources.findById(command.sourceId())
                .orElseThrow(() -> KnowledgeExceptions.knowledgeSourceNotFound(command.sourceId()));

        String idempotencyKey = "reindex-source:" + command.sourceId() + ":" + source.sourceVersionRefId();
        Instant now = Instant.now();

        KnowledgeIndexJob job = new KnowledgeIndexJob(UUID.randomUUID(),
                source.workspaceId(), source.projectId(), source.id(), null,
                IndexJobType.SOURCE_INDEX, IndexJobStatus.RUNNING,
                idempotencyKey, null, 0, 0, 0, 0, null, null, now, now, null, command.requestedByActorId());
        job = jobs.save(job);

        try {
            indexingService.invalidateSource(source.workspaceId(), source.sourceRefId());
            Instant completedAt = Instant.now();
            job = new KnowledgeIndexJob(job.id(), job.workspaceId(), job.projectId(), job.sourceId(), null,
                    IndexJobType.SOURCE_INDEX, IndexJobStatus.SUCCEEDED,
                    idempotencyKey, null, 1, 1, 1, 0, null, null, now, now, completedAt, command.requestedByActorId());
        } catch (Exception e) {
            Instant completedAt = Instant.now();
            job = new KnowledgeIndexJob(job.id(), job.workspaceId(), job.projectId(), job.sourceId(), null,
                    IndexJobType.SOURCE_INDEX, IndexJobStatus.FAILED,
                    idempotencyKey, null, 1, 1, 0, 1, "REINDEX_ERROR", null, now, now, completedAt, command.requestedByActorId());
        }
        job = jobs.save(job);
        return toResponse(job);
    }

    private IndexJobResponse toResponse(KnowledgeIndexJob job) {
        return new IndexJobResponse(job.id(), job.workspaceId(), job.projectId(), job.sourceId(),
                job.jobType().name(), job.jobStatus().name(), job.idempotencyKey(),
                job.processedCount(), job.successCount(), job.failureCount(), job.queuedAt());
    }
}
