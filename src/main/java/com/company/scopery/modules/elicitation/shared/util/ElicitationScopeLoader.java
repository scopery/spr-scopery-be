package com.company.scopery.modules.elicitation.shared.util;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ElicitationScopeLoader {

    private final NamedParameterJdbcTemplate jdbc;

    public ElicitationScopeLoader(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record ScopeContext(
            String scopeName,
            String requirementsJson,
            String functionsJson,
            String useCasesJson,
            String screensJson,
            String apisJson,
            String entitiesJson,
            String componentsJson,
            String nfrJson
    ) {}

    public ScopeContext load(UUID projectId, UUID scopePackageId) {
        String scopeName = loadScopeName(scopePackageId);
        String requirementsJson = loadRequirementsJson(projectId, scopePackageId);
        List<UUID> functionIds = loadFunctionIds(projectId, scopePackageId);
        String functionsJson = functionIds.isEmpty() ? "[]" : loadFunctionsJson(functionIds);
        String useCasesJson = functionIds.isEmpty() ? "[]" : loadUseCasesJson(functionIds);
        String screensJson = functionIds.isEmpty() ? "[]" : loadScreensJson(functionIds);
        String apisJson = functionIds.isEmpty() ? "[]" : loadApisJson(functionIds);
        String nfrJson = loadNfrJson(projectId);
        return new ScopeContext(scopeName, requirementsJson, functionsJson, useCasesJson,
                screensJson, apisJson, "[]", "[]", nfrJson);
    }

    private String loadScopeName(UUID scopePackageId) {
        String sql = "SELECT name FROM scope_package WHERE id = :id LIMIT 1";
        List<String> names = jdbc.query(sql, new MapSqlParameterSource("id", scopePackageId),
                (rs, i) -> rs.getString("name"));
        return names.isEmpty() ? "Unknown Scope" : names.get(0);
    }

    private String loadRequirementsJson(UUID projectId, UUID scopePackageId) {
        String sql = """
                SELECT json_agg(json_build_object(
                    'id', r.id,
                    'code', r.code,
                    'title', r.title,
                    'description', r.description,
                    'type', r.requirement_type,
                    'priority', r.priority
                )) AS result
                FROM requirements_requirement r
                WHERE r.project_id = :projectId AND r.scope_package_id = :scopePackageId
                  AND r.status NOT IN ('ARCHIVED', 'OBSOLETE')
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("projectId", projectId)
                .addValue("scopePackageId", scopePackageId);
        return queryJsonArray(sql, params);
    }

    private List<UUID> loadFunctionIds(UUID projectId, UUID scopePackageId) {
        String sql = """
                SELECT DISTINCT rf.function_id
                FROM app_requirement_function rf
                JOIN requirements_requirement r ON r.id = rf.requirement_id
                WHERE r.project_id = :projectId AND r.scope_package_id = :scopePackageId
                  AND r.status NOT IN ('ARCHIVED', 'OBSOLETE')
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("projectId", projectId)
                .addValue("scopePackageId", scopePackageId);
        return jdbc.query(sql, params, (rs, i) -> (UUID) rs.getObject("function_id"));
    }

    private String loadFunctionsJson(List<UUID> functionIds) {
        String sql = """
                SELECT json_agg(json_build_object(
                    'id', f.id,
                    'code', f.code,
                    'title', f.title,
                    'description', f.description,
                    'type', f.type,
                    'priority', f.priority,
                    'acceptanceCriteria', f.acceptance_criteria,
                    'businessRules', (
                        SELECT COALESCE(json_agg(json_build_object(
                            'code', br.code,
                            'title', br.title,
                            'description', br.description,
                            'severity', br.severity
                        ) ORDER BY br.code), '[]'::json)
                        FROM app_business_rule br
                        WHERE br.functional_item_id = f.id
                          AND br.status NOT IN ('ARCHIVED', 'DEPRECATED')
                    )
                )) AS result
                FROM app_functional_item f
                WHERE f.id = ANY(:ids)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("ids", functionIds.toArray(new UUID[0]));
        return queryJsonArray(sql, params);
    }

    private String loadUseCasesJson(List<UUID> functionIds) {
        String sql = """
                SELECT json_agg(json_build_object(
                    'id', u.id,
                    'key', u.key,
                    'name', u.name,
                    'goal', u.goal,
                    'primaryActor', u.primary_actor_name,
                    'trigger', u.trigger_text,
                    'conditions', (
                        SELECT COALESCE(json_agg(json_build_object(
                            'type', c.condition_type,
                            'content', c.content
                        ) ORDER BY c.display_order), '[]'::json)
                        FROM app_use_case_condition c
                        WHERE c.use_case_id = u.id
                    ),
                    'businessRules', (
                        SELECT COALESCE(json_agg(json_build_object(
                            'code', br.rule_code,
                            'description', br.description
                        ) ORDER BY br.display_order), '[]'::json)
                        FROM app_use_case_business_rule br
                        WHERE br.use_case_id = u.id
                    ),
                    'acceptanceCriteria', (
                        SELECT COALESCE(json_agg(json_build_object(
                            'title', ac.title,
                            'given', ac.given_text,
                            'when', ac.when_text,
                            'then', ac.then_text
                        ) ORDER BY ac.display_order), '[]'::json)
                        FROM app_use_case_acceptance_criterion ac
                        WHERE ac.use_case_id = u.id
                    )
                )) AS result
                FROM (
                    SELECT DISTINCT ON (uc.id) uc.id, uc.key, uc.name, uc.goal,
                           uc.primary_actor_name, uc.trigger_text
                    FROM app_use_case_supporting_function ucsf
                    JOIN app_use_case uc ON uc.id = ucsf.use_case_id
                    WHERE ucsf.function_id = ANY(:ids)
                ) u
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("ids", functionIds.toArray(new UUID[0]));
        return queryJsonArray(sql, params);
    }

    private String loadScreensJson(List<UUID> functionIds) {
        String sql = """
                SELECT json_agg(json_build_object(
                    'id', s.id,
                    'code', s.code,
                    'name', s.name
                )) AS result
                FROM (
                    SELECT DISTINCT ON (scr.id) scr.id, scr.code, scr.name
                    FROM app_function_screen fs
                    JOIN app_registry_screen scr ON scr.id = fs.screen_id
                    WHERE fs.function_id = ANY(:ids)
                ) s
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("ids", functionIds.toArray(new UUID[0]));
        return queryJsonArray(sql, params);
    }

    private String loadApisJson(List<UUID> functionIds) {
        String sql = """
                SELECT json_agg(json_build_object(
                    'id', a.id,
                    'method', a.method,
                    'path', a.path_pattern,
                    'name', a.name
                )) AS result
                FROM (
                    SELECT DISTINCT ON (ep.id) ep.id, ep.method, ep.path_pattern, ep.name
                    FROM app_function_api fa
                    JOIN app_registry_api_endpoint ep ON ep.id = fa.api_endpoint_id
                    WHERE fa.function_id = ANY(:ids)
                ) a
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("ids", functionIds.toArray(new UUID[0]));
        return queryJsonArray(sql, params);
    }

    private String loadNfrJson(UUID projectId) {
        String sql = """
                SELECT json_agg(json_build_object(
                    'code', n.code,
                    'title', n.title,
                    'description', n.description,
                    'category', n.category,
                    'priority', n.priority,
                    'targetMetric', n.target_metric
                )) AS result
                FROM app_non_functional_item n
                WHERE n.project_id = :projectId
                  AND n.status NOT IN ('ARCHIVED', 'DEPRECATED')
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("projectId", projectId);
        return queryJsonArray(sql, params);
    }

    private String queryJsonArray(String sql, MapSqlParameterSource params) {
        List<String> results = jdbc.query(sql, params, (rs, i) -> rs.getString("result"));
        String result = results.isEmpty() ? null : results.get(0);
        return result != null ? result : "[]";
    }
}
