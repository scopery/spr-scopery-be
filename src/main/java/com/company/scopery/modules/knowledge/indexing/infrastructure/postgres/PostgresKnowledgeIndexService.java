package com.company.scopery.modules.knowledge.indexing.infrastructure.postgres;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostgresKnowledgeIndexService {

    private static final Logger log = LoggerFactory.getLogger(PostgresKnowledgeIndexService.class);

    private static final String UPDATE_SQL = """
            UPDATE knowledge_chunk SET
                embedding      = CAST(? AS vector),
                search_vector  = to_tsvector('simple', ?),
                title          = ?,
                language       = ?,
                workspace_id   = CAST(? AS uuid),
                project_id     = CAST(? AS uuid),
                acl_tokens     = CAST(? AS text[]),
                classification = ?,
                source_type    = ?,
                source_status  = ?,
                app_route      = ?,
                indexed_at     = ?
            WHERE id = CAST(? AS uuid)
            """;

    private final JdbcTemplate jdbc;

    public PostgresKnowledgeIndexService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional
    public int bulkIndex(List<KnowledgeChunkIndexRecord> records) {
        if (records.isEmpty()) return 0;

        java.sql.Timestamp now = java.sql.Timestamp.from(Instant.now());

        int[][] batchCounts = jdbc.batchUpdate(UPDATE_SQL, records, records.size(),
                (ps, r) -> {
                    ps.setString(1, embeddingToString(r.embedding()));
                    ps.setString(2, buildSearchContent(r.title(), r.content()));
                    ps.setString(3, r.title());
                    ps.setString(4, r.language());
                    ps.setString(5, r.workspaceId().toString());
                    if (r.projectId() != null) {
                        ps.setString(6, r.projectId().toString());
                    } else {
                        ps.setNull(6, Types.VARCHAR);
                    }
                    ps.setString(7, toPostgresArrayLiteral(r.aclTokens()));
                    ps.setString(8, r.classification());
                    ps.setString(9, r.sourceType());
                    ps.setString(10, r.sourceStatus());
                    ps.setString(11, r.appRoute());
                    ps.setTimestamp(12, now);
                    ps.setString(13, r.chunkId().toString());
                });

        int total = 0;
        for (int[] chunk : batchCounts) {
            for (int c : chunk) total += c;
        }
        log.debug("Indexed {} knowledge chunks into PostgreSQL pgvector", total);
        return total;
    }

    /** Deletion is handled upstream by KnowledgeChunkRepository.markSupersededBySourceId() */
    public void deleteBySourceId(String sourceId) {
        // no-op: superseded chunks are already cleared before bulkIndex is called
    }

    private static String embeddingToString(float[] embedding) {
        if (embedding == null || embedding.length == 0) return null;
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(embedding[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    private static String buildSearchContent(String title, String content) {
        StringBuilder sb = new StringBuilder();
        if (title != null && !title.isBlank()) sb.append(title).append(' ');
        if (content != null) sb.append(content);
        return sb.toString();
    }

    private static String toPostgresArrayLiteral(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) return "{}";
        return tokens.stream()
                .map(t -> "\"" + t.replace("\\", "\\\\").replace("\"", "\\\"") + "\"")
                .collect(Collectors.joining(",", "{", "}"));
    }
}
