package com.company.scopery.modules.traceability.aimapping.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "scopery.ai-mapping")
public class AiMappingProperties {

    /** Top-K candidates sent to the AI per source item. */
    private int candidateLimit = 5;

    /** Max source items per LLM batch call. */
    private int batchSize = 15;

    /** Embedding model used for compact summary embeddings. */
    private String embeddingModel = "text-embedding-3-small";

    /**
     * pg_trgm title similarity threshold for clear-match pre-screening.
     * Sources whose best candidate exceeds this score skip the AI call and are
     * auto-suggested with HIGH confidence.
     */
    private double clearMatchThreshold = 0.90;

    /** Cron schedule for AiMappingIndexJob (rebuilds stale summaries). */
    private String indexCron = "0 0 */4 * * *";

    public int getCandidateLimit() { return candidateLimit; }
    public void setCandidateLimit(int candidateLimit) { this.candidateLimit = candidateLimit; }

    public int getBatchSize() { return batchSize; }
    public void setBatchSize(int batchSize) { this.batchSize = batchSize; }

    public String getEmbeddingModel() { return embeddingModel; }
    public void setEmbeddingModel(String embeddingModel) { this.embeddingModel = embeddingModel; }

    public double getClearMatchThreshold() { return clearMatchThreshold; }
    public void setClearMatchThreshold(double clearMatchThreshold) { this.clearMatchThreshold = clearMatchThreshold; }

    public String getIndexCron() { return indexCron; }
    public void setIndexCron(String indexCron) { this.indexCron = indexCron; }
}
