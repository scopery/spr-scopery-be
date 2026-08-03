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

    public record CandidateResult(UUID entityId, SummaryEntityType targetType, double retrievalScore, int rank) {}

    @Transactional(readOnly = true)
    public List<UnmappedSource> findUnmappedSources(MappingRelationType relationType, UUID projectId, int limit) {
        return switch (relationType) {
            case REQUIREMENT_TO_FUNCTION -> findUnmappedRequirements(projectId, limit);
            case FUNCTION_TO_USE_CASE -> findUnmappedUseCases(projectId, limit);
            case USE_CASE_TO_TEST_CASE -> findUnmappedTestCases(projectId, limit);
        };
    }

    @Transactional(readOnly = true)
    public List<CandidateResult> findCandidates(MappingRelationType relationType, UUID projectId,
                                                  UUID sourceId, String searchText, int limit) {
        return switch (relationType) {
            case REQUIREMENT_TO_FUNCTION -> findFunctionCandidatesForRequirement(
                    projectId, sourceId, searchText, limit);
            case FUNCTION_TO_USE_CASE -> findFunctionCandidatesForUseCase(
                    projectId, sourceId, searchText, limit);
            case USE_CASE_TO_TEST_CASE -> findUseCaseCandidatesForTestCase(
                    projectId, sourceId, searchText, limit);
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
        String sql = """
                WITH
                src AS (
                    SELECT embedding FROM %1$s
                    WHERE entity_type = 'TEST_CASE' AND entity_id = ?::uuid
                ),
                fts_scored AS (
                    SELECT uc.id,
                           ts_rank(
                               to_tsvector('english', COALESCE(uc.name,'') || ' ' || COALESCE(uc.goal,'')),
                               plainto_tsquery('english', ?)
                           ) AS score
                    FROM app_use_case uc
                    WHERE uc.project_id = ?::uuid
                      AND uc.status NOT IN ('ARCHIVED', 'DEPRECATED')
                ),
                fts AS (
                    SELECT id, ROW_NUMBER() OVER (ORDER BY score DESC) AS rn
                    FROM fts_scored
                    WHERE score > 0
                    LIMIT 20
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
                all_ids AS (SELECT id FROM fts UNION SELECT id FROM vsearch),
                rrf AS (
                    SELECT a.id,
                           COALESCE(1.0 / (60.0 + f.rn), 0) + COALESCE(1.0 / (60.0 + v.rn), 0) AS rrf_score
                    FROM all_ids a
                    LEFT JOIN fts f ON f.id = a.id
                    LEFT JOIN vsearch v ON v.id = a.id
                )
                SELECT id, rrf_score, ROW_NUMBER() OVER (ORDER BY rrf_score DESC) AS final_rank
                FROM rrf
                ORDER BY rrf_score DESC
                LIMIT ?
                """.formatted(AiMappingTableNames.MAPPING_SUMMARY);
        return jdbc.query(sql,
                (rs, n) -> new CandidateResult(
                        UUID.fromString(rs.getString("id")),
                        SummaryEntityType.USE_CASE,
                        rs.getDouble("rrf_score"),
                        rs.getInt("final_rank")
                ),
                sourceId.toString(), searchText, projectId.toString(), projectId.toString(), limit);
    }

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
                + "               to_tsvector('english', COALESCE(fi.title,'') || ' ' || COALESCE(fi.description,'')),\n"
                + "               plainto_tsquery('english', ?)\n"
                + "           ) AS score\n"
                + "    FROM app_functional_item fi\n"
                + "    WHERE fi.project_id = ?::uuid\n"
                + "      AND fi.status != 'ARCHIVED'\n"
                + "),\n"
                + "fts AS (\n"
                + "    SELECT id, ROW_NUMBER() OVER (ORDER BY score DESC) AS rn\n"
                + "    FROM fts_scored\n"
                + "    WHERE score > 0\n"
                + "    LIMIT 20\n"
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
                + "all_ids AS (SELECT id FROM fts UNION SELECT id FROM vsearch),\n"
                + "rrf AS (\n"
                + "    SELECT a.id,\n"
                + "           COALESCE(1.0 / (60.0 + f.rn), 0) + COALESCE(1.0 / (60.0 + v.rn), 0) AS rrf_score\n"
                + "    FROM all_ids a\n"
                + "    LEFT JOIN fts f ON f.id = a.id\n"
                + "    LEFT JOIN vsearch v ON v.id = a.id\n"
                + ")\n"
                + "SELECT id, rrf_score, ROW_NUMBER() OVER (ORDER BY rrf_score DESC) AS final_rank\n"
                + "FROM rrf\n"
                + "ORDER BY rrf_score DESC\n"
                + "LIMIT ?";
    }

    private List<CandidateResult> queryCandidates(String sql, SummaryEntityType targetType,
                                                    UUID projectId, UUID sourceId,
                                                    String searchText, int limit) {
        return jdbc.query(sql,
                (rs, n) -> new CandidateResult(
                        UUID.fromString(rs.getString("id")),
                        targetType,
                        rs.getDouble("rrf_score"),
                        rs.getInt("final_rank")
                ),
                sourceId.toString(), searchText, projectId.toString(), projectId.toString(), limit);
    }
}
