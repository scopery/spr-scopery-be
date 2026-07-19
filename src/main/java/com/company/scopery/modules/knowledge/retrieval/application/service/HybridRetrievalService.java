package com.company.scopery.modules.knowledge.retrieval.application.service;

import com.company.scopery.modules.knowledge.indexing.infrastructure.embedding.EmbeddingProvider;
import com.company.scopery.modules.knowledge.retrieval.application.query.SearchKnowledgeQuery;
import com.company.scopery.modules.knowledge.retrieval.application.response.CitationResponse;
import com.company.scopery.modules.knowledge.retrieval.application.response.RetrievalResponse;
import com.company.scopery.modules.knowledge.retrieval.application.response.RetrievalResultItem;
import com.company.scopery.modules.knowledge.retrieval.domain.model.RetrievalTrace;
import com.company.scopery.modules.knowledge.retrieval.domain.model.RetrievalTraceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class HybridRetrievalService {

    private static final Logger log = LoggerFactory.getLogger(HybridRetrievalService.class);
    private static final int RRF_K = 60;
    private static final int LEXICAL_CANDIDATES = 50;
    private static final int VECTOR_CANDIDATES = 50;
    private static final int DEFAULT_TOP_K = 20;
    private static final int MAX_TOP_K = 50;

    /**
     * Lexical full-text search using PostgreSQL tsvector / plainto_tsquery('simple').
     * Returns up to LEXICAL_CANDIDATES rows ordered by ts_rank_cd descending.
     */
    private static final String LEXICAL_SQL = """
            SELECT
                kc.id              AS chunk_id,
                kc.plain_text      AS content,
                kc.chunk_ordinal   AS chunk_ordinal,
                kc.title           AS title,
                kc.app_route       AS app_route,
                kc.source_type     AS source_type,
                kc.source_id       AS source_id,
                ks.source_ref_id   AS source_ref_id,
                ts_rank_cd(kc.search_vector, plainto_tsquery('simple', :queryText)) AS score
            FROM knowledge_chunk kc
            JOIN knowledge_source ks ON ks.id = kc.source_id
            WHERE kc.search_vector @@ plainto_tsquery('simple', :queryText)
              AND kc.workspace_id = :workspaceId::uuid
              AND (:projectId IS NULL OR kc.project_id = :projectId::uuid)
              AND kc.is_current = true
              AND (:hasAcl = false OR kc.acl_tokens && :aclTokens::text[])
            ORDER BY score DESC
            LIMIT :limit
            """;

    /**
     * KNN vector search using pgvector cosine distance (<=>).
     * HNSW index on knowledge_chunk(embedding) accelerates this query.
     */
    private static final String VECTOR_SQL = """
            SELECT
                kc.id              AS chunk_id,
                kc.plain_text      AS content,
                kc.chunk_ordinal   AS chunk_ordinal,
                kc.title           AS title,
                kc.app_route       AS app_route,
                kc.source_type     AS source_type,
                kc.source_id       AS source_id,
                ks.source_ref_id   AS source_ref_id,
                1.0 - (kc.embedding <=> :embedding::vector) AS score
            FROM knowledge_chunk kc
            JOIN knowledge_source ks ON ks.id = kc.source_id
            WHERE kc.embedding IS NOT NULL
              AND kc.workspace_id = :workspaceId::uuid
              AND (:projectId IS NULL OR kc.project_id = :projectId::uuid)
              AND kc.is_current = true
              AND (:hasAcl = false OR kc.acl_tokens && :aclTokens::text[])
            ORDER BY kc.embedding <=> :embedding::vector ASC
            LIMIT :limit
            """;

    private final NamedParameterJdbcTemplate namedJdbc;
    private final EmbeddingProvider embeddingProvider;
    private final RetrievalTraceRepository traceRepository;
    private final String embeddingModel;

    public HybridRetrievalService(
            NamedParameterJdbcTemplate namedJdbc,
            EmbeddingProvider embeddingProvider,
            RetrievalTraceRepository traceRepository,
            @Value("${scopery.embedding.model:text-embedding-3-small}") String embeddingModel) {
        this.namedJdbc = namedJdbc;
        this.embeddingProvider = embeddingProvider;
        this.traceRepository = traceRepository;
        this.embeddingModel = embeddingModel;
    }

    public RetrievalResponse search(SearchKnowledgeQuery query) {
        long startMs = System.currentTimeMillis();
        int topK = Math.min(query.topK() != null ? query.topK() : DEFAULT_TOP_K, MAX_TOP_K);

        String normalizedQuery = query.query().strip();
        if (normalizedQuery.isBlank()) {
            return RetrievalResponse.empty(query.workspaceId(), query.projectId());
        }

        try {
            List<ScoredChunk> lexicalHits = lexicalSearch(normalizedQuery, query);
            float[] queryEmbedding = embeddingProvider.embed(List.of(normalizedQuery), embeddingModel).get(0);
            List<ScoredChunk> vectorHits = vectorSearch(queryEmbedding, query);

            List<ScoredChunk> merged = rrf(lexicalHits, vectorHits, topK);
            List<RetrievalResultItem> items = merged.stream().map(HybridRetrievalService::toResultItem).toList();

            int durationMs = (int) (System.currentTimeMillis() - startMs);
            persistTrace(query, lexicalHits.size(), vectorHits.size(), 0, items.size(), durationMs, "OK");
            return new RetrievalResponse(query.workspaceId(), query.projectId(), items, durationMs);

        } catch (Exception e) {
            log.error("Hybrid retrieval failed for workspace={}: {}", query.workspaceId(), e.getMessage());
            int durationMs = (int) (System.currentTimeMillis() - startMs);
            persistTrace(query, 0, 0, 0, 0, durationMs, "ERROR");
            return RetrievalResponse.empty(query.workspaceId(), query.projectId());
        }
    }

    private List<ScoredChunk> lexicalSearch(String queryText, SearchKnowledgeQuery query) {
        MapSqlParameterSource params = buildBaseParams(query)
                .addValue("queryText", queryText)
                .addValue("limit", LEXICAL_CANDIDATES);
        return namedJdbc.query(LEXICAL_SQL, params, ROW_MAPPER);
    }

    private List<ScoredChunk> vectorSearch(float[] embedding, SearchKnowledgeQuery query) {
        MapSqlParameterSource params = buildBaseParams(query)
                .addValue("embedding", embeddingToString(embedding))
                .addValue("limit", VECTOR_CANDIDATES);
        return namedJdbc.query(VECTOR_SQL, params, ROW_MAPPER);
    }

    private MapSqlParameterSource buildBaseParams(SearchKnowledgeQuery query) {
        boolean hasAcl = query.aclTokens() != null && !query.aclTokens().isEmpty();
        return new MapSqlParameterSource()
                .addValue("workspaceId", query.workspaceId().toString())
                .addValue("projectId", query.projectId() != null ? query.projectId().toString() : null)
                .addValue("hasAcl", hasAcl)
                .addValue("aclTokens", hasAcl ? toPostgresArrayLiteral(query.aclTokens()) : "{}");
    }

    /** RRF: score(d) = Σ 1/(k + rank(d)), k=60 */
    private static List<ScoredChunk> rrf(List<ScoredChunk> lexical, List<ScoredChunk> vector, int topK) {
        Map<String, Double> rrfScores = new LinkedHashMap<>();
        Map<String, ScoredChunk> byChunkId = new HashMap<>();

        for (int i = 0; i < lexical.size(); i++) {
            ScoredChunk chunk = lexical.get(i);
            rrfScores.merge(chunk.chunkId(), 1.0 / (RRF_K + i + 1), Double::sum);
            byChunkId.putIfAbsent(chunk.chunkId(), chunk);
        }
        for (int i = 0; i < vector.size(); i++) {
            ScoredChunk chunk = vector.get(i);
            rrfScores.merge(chunk.chunkId(), 1.0 / (RRF_K + i + 1), Double::sum);
            byChunkId.putIfAbsent(chunk.chunkId(), chunk);
        }

        return rrfScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(topK)
                .map(e -> byChunkId.get(e.getKey()).withScore(rrfScores.get(e.getKey())))
                .toList();
    }

    private static RetrievalResultItem toResultItem(ScoredChunk scored) {
        CitationResponse citation = new CitationResponse(
                scored.sourceId(), scored.sourceType(), scored.sourceRefId(),
                scored.title(), scored.appRoute(), scored.chunkOrdinal());
        return new RetrievalResultItem(scored.chunkId(), scored.content(), scored.score(), citation);
    }

    private void persistTrace(SearchKnowledgeQuery query, int lexical, int vector, int graph,
                               int returned, int durationMs, String status) {
        try {
            Instant now = Instant.now();
            traceRepository.save(new RetrievalTrace(UUID.randomUUID(),
                    query.workspaceId(), query.projectId(), query.actorId(),
                    sha256(query.query()), "HYBRID", lexical, vector, graph,
                    returned, durationMs, status, null,
                    now, now.plusSeconds(7 * 24 * 3600)));
        } catch (Exception e) {
            log.warn("Failed to persist retrieval trace: {}", e.getMessage());
        }
    }

    private static String embeddingToString(float[] embedding) {
        if (embedding == null || embedding.length == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(embedding[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    private static String toPostgresArrayLiteral(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) return "{}";
        return tokens.stream()
                .map(t -> "\"" + t.replace("\\", "\\\\").replace("\"", "\\\"") + "\"")
                .collect(java.util.stream.Collectors.joining(",", "{", "}"));
    }

    private static String sha256(String text) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private static final org.springframework.jdbc.core.RowMapper<ScoredChunk> ROW_MAPPER =
            (rs, rowNum) -> new ScoredChunk(
                    rs.getString("chunk_id"),
                    rs.getString("content"),
                    rs.getInt("chunk_ordinal"),
                    rs.getString("title"),
                    rs.getString("app_route"),
                    rs.getString("source_type"),
                    rs.getString("source_id"),
                    rs.getString("source_ref_id"),
                    rs.getDouble("score")
            );

    private record ScoredChunk(
            String chunkId,
            String content,
            int chunkOrdinal,
            String title,
            String appRoute,
            String sourceType,
            String sourceId,
            String sourceRefId,
            double score
    ) {
        ScoredChunk withScore(double newScore) {
            return new ScoredChunk(chunkId, content, chunkOrdinal, title, appRoute, sourceType, sourceId, sourceRefId, newScore);
        }
    }
}
