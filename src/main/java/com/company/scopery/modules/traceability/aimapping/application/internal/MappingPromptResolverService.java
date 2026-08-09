package com.company.scopery.modules.traceability.aimapping.application.internal;

import com.company.scopery.modules.traceability.aimapping.shared.error.AiMappingExceptions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MappingPromptResolverService {

    private final JdbcTemplate jdbc;

    public MappingPromptResolverService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record ResolvedPrompt(
            String systemPrompt,
            String userPromptTemplate,
            BigDecimal temperature,
            Integer maxTokens,
            String providerCode,
            String modelId
    ) {}

    @Transactional(readOnly = true)
    public ResolvedPrompt resolveByTemplateCode(String templateCode) {
        String sql = """
                SELECT pv.system_prompt, pv.user_prompt_template, pv.temperature, pv.max_tokens,
                       p.code   AS provider_code,
                       m.provider_model_id AS model_id
                FROM aiagent_prompt_version pv
                INNER JOIN aiagent_prompt_template pt  ON pt.id = pv.template_id
                INNER JOIN aiagent_agent a              ON a.id  = pt.agent_id
                INNER JOIN aiagent_model_deployment d   ON d.id  = a.default_model_deployment_id
                INNER JOIN aiagent_model m              ON m.id  = d.model_id
                INNER JOIN aiagent_provider p           ON p.id  = m.provider_id
                WHERE pt.code = ? AND pv.status = 'ACTIVE'
                ORDER BY pv.version_number DESC
                LIMIT 1
                """;
        List<ResolvedPrompt> results = jdbc.query(sql,
                (rs, rowNum) -> new ResolvedPrompt(
                        rs.getString("system_prompt"),
                        rs.getString("user_prompt_template"),
                        rs.getBigDecimal("temperature"),
                        rs.getObject("max_tokens", Integer.class),
                        rs.getString("provider_code"),
                        rs.getString("model_id")
                ),
                templateCode);

        if (results.isEmpty()) {
            throw AiMappingExceptions.promptNotFound(templateCode);
        }
        return results.get(0);
    }
}
