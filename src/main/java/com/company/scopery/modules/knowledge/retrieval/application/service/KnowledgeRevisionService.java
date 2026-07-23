package com.company.scopery.modules.knowledge.retrieval.application.service;

import com.company.scopery.platform.config.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Returns a short revision key that changes whenever project knowledge content changes.
 * Used as a segment in the retrieval cache key to auto-invalidate stale results.
 * Cached for 60s so a burst of requests doesn't hammer the DB.
 */
@Service
public class KnowledgeRevisionService {

    private static final String REVISION_SQL = """
            SELECT COALESCE(MAX(ks.version)::text, '0') || '_' || COUNT(kc.id)::text
            FROM knowledge_source ks
            JOIN knowledge_chunk kc ON kc.source_id = ks.id
            WHERE kc.project_id = :projectId
              AND kc.is_current = true
            """;

    private final NamedParameterJdbcTemplate namedJdbc;

    public KnowledgeRevisionService(NamedParameterJdbcTemplate namedJdbc) {
        this.namedJdbc = namedJdbc;
    }

    @Cacheable(value = CacheConfig.AI_KNOWLEDGE_REVISION, key = "#projectId.toString()")
    public String getRevisionKey(UUID projectId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("projectId", projectId.toString());
        String result = namedJdbc.queryForObject(REVISION_SQL, params, String.class);
        return result != null ? result : "0_0";
    }
}
