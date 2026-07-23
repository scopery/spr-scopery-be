package com.company.scopery.modules.knowledge.queryrewriter.application.service;

import com.company.scopery.integration.ai.AiProviderAdapter;
import com.company.scopery.integration.ai.AiProviderAdapterRegistry;
import com.company.scopery.integration.ai.AiProviderRequest;
import com.company.scopery.integration.ai.AiProviderResponse;
import com.company.scopery.modules.knowledge.queryrewriter.domain.model.KnowledgeQueryRewriterConfig;
import com.company.scopery.modules.knowledge.queryrewriter.domain.model.KnowledgeQueryRewriterConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class KnowledgeQueryRewriterService {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeQueryRewriterService.class);

    private static final String DEFAULT_PROMPT_TEMPLATE = """
            Extract the most important search keywords from the following query.
            Return ONLY the keywords in English, space-separated, no punctuation, no explanation.
            Remove question words and stop words (what, is, are, how, does, the, a, an, do, can, where, when, who, which, about, tell, me, give, show, explain).
            Query: {query}
            """;

    private final KnowledgeQueryRewriterConfigRepository configRepository;
    private final AiProviderAdapterRegistry providerAdapterRegistry;

    public KnowledgeQueryRewriterService(
            KnowledgeQueryRewriterConfigRepository configRepository,
            AiProviderAdapterRegistry providerAdapterRegistry) {
        this.configRepository = configRepository;
        this.providerAdapterRegistry = providerAdapterRegistry;
    }

    public String rewrite(String originalQuery, UUID workspaceId) {
        KnowledgeQueryRewriterConfig config = configRepository.findByWorkspaceId(workspaceId).orElse(null);
        if (config == null || !config.enabled() || config.provider() == null || config.model() == null) {
            return originalQuery;
        }
        try {
            String prompt = (config.promptTemplate() != null && !config.promptTemplate().isBlank()
                    ? config.promptTemplate() : DEFAULT_PROMPT_TEMPLATE)
                    .replace("{query}", originalQuery);

            AiProviderAdapter adapter = providerAdapterRegistry.getAdapter(config.provider());
            AiProviderRequest request = new AiProviderRequest(
                    null, config.model(), prompt,
                    new BigDecimal("0.0"),
                    config.maxTokens());

            AiProviderResponse response = adapter.call(request);
            String rewritten = response.outputText() != null ? response.outputText().strip() : null;
            if (rewritten == null || rewritten.isBlank()) {
                return originalQuery;
            }
            log.debug("[QueryRewriter] workspace={} original='{}' rewritten='{}'", workspaceId, originalQuery, rewritten);
            return rewritten;
        } catch (Exception e) {
            log.warn("[QueryRewriter] Rewrite failed for workspace={}: {} — falling back to original query", workspaceId, e.getMessage());
            return originalQuery;
        }
    }
}
