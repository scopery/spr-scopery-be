package com.company.scopery.modules.elicitation.shared.util;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
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
            String componentsJson
    ) {}

    public ScopeContext load(UUID projectId, UUID scopePackageId) {
        String scopeName = loadScopeName(scopePackageId);
        String requirementsJson = loadRequirementsJson(projectId, scopePackageId);
        List<UUID> functionIds = loadFunctionIds(projectId, scopePackageId);
        String functionsJson = functionIds.isEmpty() ? "[]" : loadFunctionsJson(functionIds);
        String useCasesJson = functionIds.isEmpty() ? "[]" : loadUseCasesJson(functionIds);
        String screensJson = functionIds.isEmpty() ? "[]" : loadScreensJson(functionIds);
        String apisJson = functionIds.isEmpty() ? "[]" : loadApisJson(functionIds);
        String entitiesJson = "[]";
        String componentsJson = "[]";
        return new ScopeContext(scopeName, requirementsJson, functionsJson, useCasesJson,
                screensJson, apisJson, entitiesJson, componentsJson);
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
                    'type', f.type
                )) AS result
                FROM app_functional_item f
                WHERE f.id = ANY(:ids)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("ids", functionIds.toArray(new UUID[0]));
        return queryJsonArray(sql, params);
    }

    private String loadUseCasesJson(List<UUID> functionIds) {
        String sql = """
                SELECT json_agg(DISTINCT json_build_object(
                    'id', uc.id,
                    'code', uc.code,
                    'title', uc.title,
                    'description', uc.description
                )) AS result
                FROM app_use_case_supporting_function ucsf
                JOIN app_use_case uc ON uc.id = ucsf.use_case_id
                WHERE ucsf.function_id = ANY(:ids)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("ids", functionIds.toArray(new UUID[0]));
        return queryJsonArray(sql, params);
    }

    private String loadScreensJson(List<UUID> functionIds) {
        String sql = """
                SELECT json_agg(DISTINCT json_build_object(
                    'id', s.id,
                    'code', s.code,
                    'name', s.name,
                    'description', s.description
                )) AS result
                FROM app_function_screen fs
                JOIN app_registry_screen s ON s.id = fs.screen_id
                WHERE fs.function_id = ANY(:ids)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("ids", functionIds.toArray(new UUID[0]));
        return queryJsonArray(sql, params);
    }

    private String loadApisJson(List<UUID> functionIds) {
        String sql = """
                SELECT json_agg(DISTINCT json_build_object(
                    'id', a.id,
                    'method', a.method,
                    'path', a.path,
                    'summary', a.summary
                )) AS result
                FROM app_function_api fa
                JOIN app_registry_api_endpoint a ON a.id = fa.api_endpoint_id
                WHERE fa.function_id = ANY(:ids)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("ids", functionIds.toArray(new UUID[0]));
        return queryJsonArray(sql, params);
    }

    private String queryJsonArray(String sql, MapSqlParameterSource params) {
        List<String> results = jdbc.query(sql, params, (rs, i) -> rs.getString("result"));
        String result = results.isEmpty() ? null : results.get(0);
        return result != null ? result : "[]";
    }
}
