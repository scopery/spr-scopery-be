package com.company.scopery.modules.knowledge.indexing.application.action;

import com.company.scopery.modules.knowledge.indexing.application.response.IndexProjectStatusResponse;
import com.company.scopery.modules.knowledge.indexing.infrastructure.embedding.EmbeddingProvider;
import com.company.scopery.modules.knowledge.indexing.infrastructure.postgres.PostgresKnowledgeIndexService;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeSourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class FillMissingChunkEmbeddingsAction {

    private static final Logger log = LoggerFactory.getLogger(FillMissingChunkEmbeddingsAction.class);
    private static final int BATCH_LIMIT = 200;

    private final PostgresKnowledgeIndexService postgresIndexService;
    private final EmbeddingProvider embeddingProvider;
    private final KnowledgeSourceRepository sources;
    private final String embeddingModel;

    public FillMissingChunkEmbeddingsAction(
            PostgresKnowledgeIndexService postgresIndexService,
            EmbeddingProvider embeddingProvider,
            KnowledgeSourceRepository sources,
            @Value("${scopery.embedding.model:text-embedding-3-small}") String embeddingModel) {
        this.postgresIndexService = postgresIndexService;
        this.embeddingProvider = embeddingProvider;
        this.sources = sources;
        this.embeddingModel = embeddingModel;
    }

    @Transactional
    public IndexProjectStatusResponse execute(UUID projectId) {
        List<PostgresKnowledgeIndexService.MissingEmbeddingChunk> missing =
                postgresIndexService.findMissingEmbeddingChunks(projectId, BATCH_LIMIT);

        if (missing.isEmpty()) {
            log.info("[FillEmbeddings] No missing embeddings for project={}", projectId);
        } else {
            log.info("[FillEmbeddings] Filling {} missing embeddings for project={}", missing.size(), projectId);
            try {
                List<String> texts = missing.stream()
                        .map(PostgresKnowledgeIndexService.MissingEmbeddingChunk::plainText)
                        .toList();
                List<float[]> embeddings = embeddingProvider.embed(texts, embeddingModel);
                int updated = postgresIndexService.bulkUpdateEmbeddings(missing, embeddings);
                log.info("[FillEmbeddings] Updated {} embeddings for project={}", updated, projectId);
            } catch (Exception e) {
                log.error("[FillEmbeddings] Embedding failed for project={}: {}", projectId, e.getMessage());
                throw e;
            }
        }

        int indexedSources = sources.findByProjectId(projectId).size();
        int total = postgresIndexService.countTotalChunksByProjectId(projectId);
        int embedded = postgresIndexService.countEmbeddedChunksByProjectId(projectId);
        return new IndexProjectStatusResponse(projectId, indexedSources, total, embedded, total - embedded, total > 0 && embedded == total);
    }
}
