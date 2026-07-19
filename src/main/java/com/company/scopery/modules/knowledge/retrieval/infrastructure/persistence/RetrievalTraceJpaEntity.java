package com.company.scopery.modules.knowledge.retrieval.infrastructure.persistence;

import com.company.scopery.modules.knowledge.shared.constant.KnowledgeTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = KnowledgeTableNames.KNOWLEDGE_RETRIEVAL_TRACE)
@Getter @Setter @NoArgsConstructor
public class RetrievalTraceJpaEntity {

    @Id
    private UUID id;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "actor_id")
    private UUID actorId;

    @Column(name = "query_hash", nullable = false, length = 64)
    private String queryHash;

    @Column(name = "retrieval_mode", nullable = false, length = 32)
    private String retrievalMode;

    @Column(name = "lexical_candidate_count", nullable = false)
    private Integer lexicalCandidateCount;

    @Column(name = "vector_candidate_count", nullable = false)
    private Integer vectorCandidateCount;

    @Column(name = "graph_candidate_count", nullable = false)
    private Integer graphCandidateCount;

    @Column(name = "returned_count", nullable = false)
    private Integer returnedCount;

    @Column(name = "duration_ms", nullable = false)
    private Integer durationMs;

    @Column(name = "result_status", nullable = false, length = 24)
    private String resultStatus;

    @Column(name = "error_code", length = 80)
    private String errorCode;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;
}
