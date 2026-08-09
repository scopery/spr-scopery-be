package com.company.scopery.modules.specpack.shared.util;

import com.company.scopery.modules.traceability.aimapping.application.internal.MappingPromptResolverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SpecPackPromptBuilder {

    private static final Logger log = LoggerFactory.getLogger(SpecPackPromptBuilder.class);
    private static final String SECTION_TEMPLATE_CODE = "SPEC_PACK_SECTION_V1";

    private final MappingPromptResolverService promptResolver;
    private final ObjectMapper objectMapper;

    public SpecPackPromptBuilder(MappingPromptResolverService promptResolver,
                                  ObjectMapper objectMapper) {
        this.promptResolver = promptResolver;
        this.objectMapper = objectMapper;
    }

    public record SectionPrompt(String systemPrompt, String userPrompt, int maxTokens) {}

    @SuppressWarnings("unchecked")
    public SectionPrompt buildSectionPrompt(SpecPackScopeLoader.ScopeContext scope,
                                             Map<String, Object> section,
                                             String previousBlocksJson) {
        MappingPromptResolverService.ResolvedPrompt resolved;
        try {
            resolved = promptResolver.resolveByTemplateCode(SECTION_TEMPLATE_CODE);
        } catch (Exception e) {
            log.warn("[SpecPackPromptBuilder] Prompt template {} not found in DB", SECTION_TEMPLATE_CODE);
            throw new IllegalStateException("Prompt template not configured: " + SECTION_TEMPLATE_CODE, e);
        }

        String title = stringOrEmpty(section.get("title"));
        String description = stringOrEmpty(section.get("description"));
        String blockTypesJson = serializeBlockTypes(section.get("blockTypes"));

        String userPrompt = resolved.userPromptTemplate()
                .replace("{{SECTION_TITLE}}", title)
                .replace("{{SECTION_DESCRIPTION}}", description)
                .replace("{{EXPECTED_BLOCK_TYPES}}", blockTypesJson)
                .replace("{{SCOPE_CONTEXT_JSON}}", buildScopeContextJson(scope))
                .replace("{{PREVIOUS_BLOCKS_JSON}}", previousBlocksJson != null ? previousBlocksJson : "[]");

        String systemPrompt = resolved.systemPrompt() != null ? resolved.systemPrompt() : "";
        int maxTokens = resolved.maxTokens() != null ? resolved.maxTokens() : 4096;

        return new SectionPrompt(systemPrompt, userPrompt, maxTokens);
    }

    private String buildScopeContextJson(SpecPackScopeLoader.ScopeContext scope) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "scopeName", scope.scopeName(),
                    "requirements", objectMapper.readTree(scope.requirementsJson()),
                    "functions", objectMapper.readTree(scope.functionsJson()),
                    "useCases", objectMapper.readTree(scope.useCasesJson()),
                    "screens", objectMapper.readTree(scope.screensJson()),
                    "apis", objectMapper.readTree(scope.apisJson()),
                    "answeredClarifications", objectMapper.readTree(scope.answeredClarificationsJson())
            ));
        } catch (Exception e) {
            return "{}";
        }
    }

    @SuppressWarnings("unchecked")
    private String serializeBlockTypes(Object blockTypes) {
        try {
            if (blockTypes instanceof List<?> list) {
                return objectMapper.writeValueAsString(list);
            }
            return "[]";
        } catch (Exception e) {
            return "[]";
        }
    }

    private String stringOrEmpty(Object val) {
        return val != null ? val.toString() : "";
    }
}
