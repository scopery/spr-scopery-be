package com.company.scopery.modules.traceability.aimapping.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConfigurationProperties(prefix = "scopery.ai-mapping")
public class AiMappingProperties {

    /** UUID of the ModelDeployment to use when generating mapping suggestions. */
    private UUID defaultModelDeploymentId;

    /** Candidate limit sent to the AI per source item (top-K). */
    private int candidateLimit = 5;

    /** Batch size: max source items per LLM request. */
    private int batchSize = 15;

    /** Embedding model code used when generating compact summary embeddings. */
    private String embeddingModel = "text-embedding-3-small";

    public UUID getDefaultModelDeploymentId() { return defaultModelDeploymentId; }
    public void setDefaultModelDeploymentId(UUID defaultModelDeploymentId) {
        this.defaultModelDeploymentId = defaultModelDeploymentId;
    }

    public int getCandidateLimit() { return candidateLimit; }
    public void setCandidateLimit(int candidateLimit) { this.candidateLimit = candidateLimit; }

    public int getBatchSize() { return batchSize; }
    public void setBatchSize(int batchSize) { this.batchSize = batchSize; }

    public String getEmbeddingModel() { return embeddingModel; }
    public void setEmbeddingModel(String embeddingModel) { this.embeddingModel = embeddingModel; }
}
