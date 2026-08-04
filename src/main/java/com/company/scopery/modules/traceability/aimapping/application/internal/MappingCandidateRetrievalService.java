package com.company.scopery.modules.traceability.aimapping.application.internal;

import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingRelationType;
import com.company.scopery.modules.traceability.aimapping.shared.constant.AiMappingTableNames;
import com.company.scopery.modules.traceability.aimapping.summary.domain.enums.SummaryEntityType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MappingCandidateRetrievalService {

    private final JdbcTemplate jdbc;

    public MappingCandidateRetrievalService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record UnmappedSource(UUID id, SummaryEntityType sourceType, int entityVersion, String searchText) {}

    /** titleSimilarity: pg_trgm similarity(source_title, candidate_title), 0.0 when trgm had no match. */
    public record CandidateResult(UUID entityId, SummaryEntityType targetType,
                                   double retrievalScore, int rank, double titleSimilarity) {}

    @Transactional(readOnly = true)
    public List<UnmappedSource> findUnmappedSources(MappingRelationType relationType, UUID projectId, int limit) {
        return switch (relationType) {
            case REQUIREMENT_TO_FUNCTION -> findUnmappedRequirements(projectId, limit);
            case FUNCTION_TO_USE_CASE    -> findUnmappedUseCases(projectId, limit);
            case USE_CASE_TO_TEST_CASE   -> findUnmappedTestCases(projectId, limit);
        };
    }

    @Transactional(readOnly = true)
    public List<CandidateResult> findCandidates(MappingRelationType relationType, UUID projectId,
                                                  UUID sourceId, String searchText, int limit) {
        return switch (relationType) {
            case REQUIREMENT_TO_FUNCTION -> findFunctionCandidatesForRequirement(projectId, sourceId, searchText, limit);
            case FUNCTION_TO_USE_CASE    -> findFunctionCandidatesForUseCase(projectId, sourceId, searchText, limit);
            case USE_CASE_TO_TEST_CASE   -> findUseCaseCandidatesForTestCase(projectId, sourceId, searchText, limit);
        };
    }

    private List<UnmappedSource> findUnmappedRequirements(UUID projectId, int limit) {
        String sql = """
                SELECT r.id, COALESCE(r.version, 0) AS version, r.title
                FROM requirements_requirement r
                WHERE r.project_id = ?::uuid
                  AND r.status NOT IN ('ARCHIVED', 'REJECTED', 'DEFERRED')
                  AND NOT EXISTS (
                      SELECT 1 FROM app_requirement_function arf
                      WHERE arf.requirement_id = r.id
                  )
                ORDER BY r.created_at
                LIMIT ?
                """;
        return jdbc.query(sql, (rs, n) -> new UnmappedSource(
                UUID.fromString(rs.getString("id")),
                SummaryEntityType.REQUIREMENT,
                rs.getInt("version"),
                rs.getString("title")
        ), projectId.toString(), limit);
    }

    private List<UnmappedSource> findUnmappedUseCases(UUID projectId, int limit) {
        String sql = """
                SELECT uc.id, uc.version, COALESCE(uc.name,'') || ' ' || COALESCE(uc.goal,'') AS search_text
                FROM app_use_case uc
                WHERE uc.project_id = ?::uuid
                  AND uc.status NOT IN ('ARCHIVED', 'DEPRECATED')
                  AND uc.primary_function_id IS NULL
                ORDER BY uc.created_at
                LIMIT ?
                """;
        return jdbc.query(sql, (rs, n) -> new UnmappedSource(
                UUID.fromString(rs.getString("id")),
                SummaryEntityType.USE_CASE,
                rs.getInt("version"),
                rs.getString("search_text")
        ), projectId.toString(), limit);
    }

    private List<UnmappedSource> findUnmappedTestCases(UUID projectId, int limit) {
        String sql = """
                SELECT tc.id, tc.version, tc.title
                FROM quality_test_case tc
                WHERE tc.project_id = ?::uuid
                  AND tc.status != 'ARCHIVED'
                  AND tc.type = 'FUNCTIONAL'
                  AND tc.use_case_id IS NULL
                ORDER BY tc.created_at
                LIMIT ?
                """;
        return jdbc.query(sql, (rs, n) -> new UnmappedSource(
                UUID.fromString(rs.getString("id")),
                SummaryEntityType.TEST_CASE,
                rs.getInt("version"),
                rs.getString("title")
        ), projectId.toString(), limit);
    }

    private List<CandidateResult> findFunctionCandidatesForRequirement(UUID projectId, UUID sourceId,
                                                                         String searchText, int limit) {
        String sql = buildFunctionCandidateQuery("REQUIREMENT");
        return queryCandidates(sql, SummaryEntityType.FUNCTION, projectId, sourceId, searchText, limit);
    }

    private List<CandidateResult> findFunctionCandidatesForUseCase(UUID projectId, UUID sourceId,
                                                                     String searchText, int limit) {
        String sql = buildFunctionCandidateQuery("USE_CASE");
        return queryCandidates(sql, SummaryEntityType.FUNCTION, projectId, sourceId, searchText, limit);
    }

    private List<CandidateResult> findUseCaseCandidatesForTestCase(UUID projectId, UUID sourceId,
                                                                     String searchText, int limit) {
        String t = AiMappingTableNames.MAPPING_SUMMARY;
        String sql = """
                WITH
                src AS (
                    SELECT embedding FROM %1$s
                    WHERE entity_type = 'TEST_CASE' AND entity_id = ?::uuid
                ),
                fts_scored AS (
                    SELECT uc.id,
                           ts_rank(
                               to_tsvector('simple', COALESCE(uc.name,'') || ' ' || COALESCE(uc.goal,'')),
                               plainto_tsquery('simple', ?)
                           ) AS score
                    FROM app_use_case uc
                    WHERE uc.project_id = ?::uuid
                      AND uc.status NOT IN ('ARCHIVED', 'DEPRECATED')
                ),
                fts AS (
                    SELECT id, ROW_NUMBER() OVER (ORDER BY score DESC) AS rn
                    FROM fts_scored WHERE score > 0 LIMIT 20
                ),
                vsearch AS (
                    SELECT ms.entity_id AS id,
                           ROW_NUMBER() OVER (ORDER BY ms.embedding <=> (SELECT embedding FROM src)) AS rn
                    FROM %1$s ms
                    INNER JOIN app_use_case uc ON uc.id = ms.entity_id
                        AND uc.project_id = ?::uuid
                        AND uc.status NOT IN ('ARCHIVED', 'DEPRECATED')
                    WHERE ms.entity_type = 'USE_CASE'
                      AND ms.embedding IS NOT NULL
                      AND (SELECT embedding FROM src) IS NOT NULL
                    ORDER BY ms.embedding <=> (SELECT embedding FROM src)
                    LIMIT 20
                ),
                trgm AS (
                    SELECT uc.id,
                           similarity(lower(uc.name), lower(?)) AS sim,
                           ROW_NUMBER() OVER (ORDER BY similarity(lower(uc.name), lower(?)) DESC) AS rn
                    FROM app_use_case uc
                    WHERE uc.project_id = ?::uuid
                      AND uc.status NOT IN ('ARCHIVED', 'DEPRECATED')
                      AND similarity(lower(uc.name), lower(?)) > 0.15
                    LIMIT 10
                ),
                all_ids AS (SELECT id FROM fts UNION SELECT id FROM vsearch UNION SELECT id FROM trgm),
                rrf AS (
                    SELECT a.id,
                           COALESCE(1.0/(60.0+f.rn),0) + COALESCE(1.0/(60.0+v.rn),0) + COALESCE(1.0/(60.0+t.rn),0) AS rrf_score,
                           COALESCE(t.sim, 0) AS title_similarity
                    FROM all_ids a
                    LEFT JOIN fts f ON f.id = a.id
                    LEFT JOIN vsearch v ON v.id = a.id
                    LEFT JOIN trgm t ON t.id = a.id
                )
                SELECT id, rrf_score, title_similarity, ROW_NUMBER() OVER (ORDER BY rrf_score DESC) AS final_rank
                FROM rrf
                ORDER BY rrf_score DESC
                LIMIT ?
                """.formatted(t);
        String st = searchText != null ? searchText : "";
        return jdbc.query(sql,
                (rs, n) -> new CandidateResult(
                        UUID.fromString(rs.getString("id")),
                        SummaryEntityType.USE_CASE,
                        rs.getDouble("rrf_score"),
                        rs.getInt("final_rank"),
                        rs.getDouble("title_similarity")
                ),
                sourceId.toString(), st, projectId.toString(), projectId.toString(),
                st, st, projectId.toString(), st,
                limit);
    }

    /**
     * Hybrid retrieval: FTS + pgvector + pg_trgm, fused via RRF (k=60).
     * trgm channel catches exact/near-exact name matches that score poorly on FTS and vector.
     */
    private String buildFunctionCandidateQuery(String sourceEntityType) {
        String t = AiMappingTableNames.MAPPING_SUMMARY;
        return "WITH\n"
                + "src AS (\n"
                + "    SELECT embedding FROM " + t + "\n"
                + "    WHERE entity_type = '" + sourceEntityType + "' AND entity_id = ?::uuid\n"
                + "),\n"
                + "fts_scored AS (\n"
                + "    SELECT fi.id,\n"
                + "           ts_rank(\n"
                + "               to_tsvector('simple', COALESCE(fi.title,'') || ' ' || COALESCE(fi.description,'')),\n"
                + "               plainto_tsquery('simple', ?)\n"
                + "           ) AS score\n"
                + "    FROM app_functional_item fi\n"
                + "    WHERE fi.project_id = ?::uuid\n"
                + "      AND fi.status != 'ARCHIVED'\n"
                + "),\n"
                + "fts AS (\n"
                + "    SELECT id, ROW_NUMBER() OVER (ORDER BY score DESC) AS rn\n"
                + "    FROM fts_scored WHERE score > 0 LIMIT 20\n"
                + "),\n"
                + "vsearch AS (\n"
                + "    SELECT ms.entity_id AS id,\n"
                + "           ROW_NUMBER() OVER (ORDER BY ms.embedding <=> (SELECT embedding FROM src)) AS rn\n"
                + "    FROM " + t + " ms\n"
                + "    INNER JOIN app_functional_item fi ON fi.id = ms.entity_id\n"
                + "        AND fi.project_id = ?::uuid\n"
                + "        AND fi.status != 'ARCHIVED'\n"
                + "    WHERE ms.entity_type = 'FUNCTION'\n"
                + "      AND ms.embedding IS NOT NULL\n"
                + "      AND (SELECT embedding FROM src) IS NOT NULL\n"
                + "    ORDER BY ms.embedding <=> (SELECT embedding FROM src)\n"
                + "    LIMIT 20\n"
                + "),\n"
                + "trgm AS (\n"
                + "    SELECT fi.id,\n"
                + "           similarity(lower(fi.title), lower(?)) AS sim,\n"
                + "           ROW_NUMBER() OVER (ORDER BY similarity(lower(fi.title), lower(?)) DESC) AS rn\n"
                + "    FROM app_functional_item fi\n"
                + "    WHERE fi.project_id = ?::uuid\n"
                + "      AND fi.status != 'ARCHIVED'\n"
                + "      AND similarity(lower(fi.title), lower(?)) > 0.15\n"
                + "    LIMIT 10\n"
                + "),\n"
                + "all_ids AS (SELECT id FROM fts UNION SELECT id FROM vsearch UNION SELECT id FROM trgm),\n"
                + "rrf AS (\n"
                + "    SELECT a.id,\n"
                + "           COALESCE(1.0/(60.0+f.rn),0) + COALESCE(1.0/(60.0+v.rn),0) + COALESCE(1.0/(60.0+t.rn),0) AS rrf_score,\n"
                + "           COALESCE(t.sim, 0) AS title_similarity\n"
                + "    FROM all_ids a\n"
                + "    LEFT JOIN fts f ON f.id = a.id\n"
                + "    LEFT JOIN vsearch v ON v.id = a.id\n"
                + "    LEFT JOIN trgm t ON t.id = a.id\n"
                + ")\n"
                + "SELECT id, rrf_score, title_similarity, ROW_NUMBER() OVER (ORDER BY rrf_score DESC) AS final_rank\n"
                + "FROM rrf\n"
                + "ORDER BY rrf_score DESC\n"
                + "LIMIT ?";
    }

    // -------------------------------------------------------------------------
    // Target pre-warming
    // -------------------------------------------------------------------------

    public record TargetRef(UUID id, SummaryEntityType targetType) {}

    @Transactional(readOnly = true)
    public List<TargetRef> findAllEligibleTargets(MappingRelationType relationType, UUID projectId) {
        return switch (relationType) {
            case REQUIREMENT_TO_FUNCTION, FUNCTION_TO_USE_CASE -> findAllActiveFunctions(projectId);
            case USE_CASE_TO_TEST_CASE -> findAllActiveUseCases(projectId);
        };
    }

    private List<TargetRef> findAllActiveFunctions(UUID projectId) {
        String sql = """
                SELECT id FROM app_functional_item
                WHERE project_id = ?::uuid AND status != 'ARCHIVED'
                """;
        return jdbc.query(sql,
                (rs, n) -> new TargetRef(UUID.fromString(rs.getString("id")), SummaryEntityType.FUNCTION),
                projectId.toString());
    }

    private List<TargetRef> findAllActiveUseCases(UUID projectId) {
        String sql = """
                SELECT id FROM app_use_case
                WHERE project_id = ?::uuid AND status NOT IN ('ARCHIVED', 'DEPRECATED')
                """;
        return jdbc.query(sql,
                (rs, n) -> new TargetRef(UUID.fromString(rs.getString("id")), SummaryEntityType.USE_CASE),
                projectId.toString());
    }

    // -------------------------------------------------------------------------
    // Internal query helper
    // Params: sourceId, searchText, projectId, projectId,
    //         searchText, searchText, projectId, searchText,   ← trgm params
    //         limit
    // -------------------------------------------------------------------------

    private List<CandidateResult> queryCandidates(String sql, SummaryEntityType targetType,
                                                    UUID projectId, UUID sourceId,
                                                    String searchText, int limit) {
        String st = searchText != null ? searchText : "";
        return jdbc.query(sql,
                (rs, n) -> new CandidateResult(
                        UUID.fromString(rs.getString("id")),
                        targetType,
                        rs.getDouble("rrf_score"),
                        rs.getInt("final_rank"),
                        rs.getDouble("title_similarity")
                ),
                sourceId.toString(), st, projectId.toString(), projectId.toString(),
                st, st, projectId.toString(), st,
                limit);
    }
}
