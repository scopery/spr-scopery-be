package com.company.scopery.modules.knowledge.retrieval.application.aitool;

import com.company.scopery.modules.aiagent.tool.application.port.AiToolExecutionContext;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolHandler;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolResult;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolResultItem;
import com.company.scopery.modules.knowledge.retrieval.application.query.SearchKnowledgeQuery;
import com.company.scopery.modules.knowledge.retrieval.application.response.RetrievalResponse;
import com.company.scopery.modules.knowledge.retrieval.application.response.RetrievalResultItem;
import com.company.scopery.modules.knowledge.retrieval.application.service.HybridRetrievalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class KnowledgeSearchAiToolHandler implements AiToolHandler {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeSearchAiToolHandler.class);
    private static final String TOOL_CODE = "knowledge.search";
    private static final String TOOL_VERSION = "v1";
    private static final int DEFAULT_TOP_K = 20;
    private static final int MAX_TOP_K = 20;
    // snippet length per item — safe, bounded
    private static final int MAX_SNIPPET_CHARS = 500;

    private final HybridRetrievalService hybridRetrievalService;

    public KnowledgeSearchAiToolHandler(HybridRetrievalService hybridRetrievalService) {
        this.hybridRetrievalService = hybridRetrievalService;
    }

    @Override
    public String toolCode() {
        return TOOL_CODE;
    }

    @Override
    public String toolVersion() {
        return TOOL_VERSION;
    }

    @Override
    public boolean readOnly() {
        return true;
    }

    @Override
    public AiToolResult execute(Map<String, Object> arguments, AiToolExecutionContext context) {
        String query = extractString(arguments, "query");
        if (query == null || query.isBlank()) {
            return AiToolResult.failure("KNOWLEDGE_QUERY_INVALID", "query argument is required and must not be blank");
        }

        // topK is bounded server-side; LLM cannot exceed MAX_TOP_K
        Integer topK = extractInt(arguments, "topK");
        int effectiveTopK = (topK == null || topK <= 0 || topK > MAX_TOP_K) ? DEFAULT_TOP_K : topK;

        // Build query with server-injected security scope — LLM cannot override
        SearchKnowledgeQuery searchQuery = new SearchKnowledgeQuery(
                context.workspaceId(),
                context.projectId(),
                context.actorId(),
                context.aclTokens() != null ? context.aclTokens() : List.of(),
                query,
                effectiveTopK
        );

        try {
            RetrievalResponse response = hybridRetrievalService.search(searchQuery);
            List<AiToolResultItem> items = mapToToolItems(response.results());
            boolean truncated = response.results().size() >= effectiveTopK;

            // retrievalTraceId comes from the first item's citation (all items share one trace in practice)
            String retrievalTraceId = items.isEmpty() ? null
                    : extractRetrievalTraceId(response.results());

            return AiToolResult.success(items.size(), truncated, retrievalTraceId, items);
        } catch (Exception e) {
            log.warn("[KnowledgeSearchAiToolHandler] Retrieval failed: {}", e.getMessage());
            return AiToolResult.failure("KNOWLEDGE_RETRIEVAL_PROVIDER_UNAVAILABLE",
                    "Knowledge retrieval unavailable — " + e.getClass().getSimpleName());
        }
    }

    private List<AiToolResultItem> mapToToolItems(List<RetrievalResultItem> results) {
        List<AiToolResultItem> items = new ArrayList<>();
        int rank = 1;
        for (RetrievalResultItem r : results) {
            String safeSnippet = r.content() != null && r.content().length() > MAX_SNIPPET_CHARS
                    ? r.content().substring(0, MAX_SNIPPET_CHARS) + "…"
                    : r.content();

            UUID chunkId = parseUuid(r.chunkId());
            UUID sourceId = r.citation() != null ? parseUuid(r.citation().sourceId()) : null;
            UUID sourceRefId = r.citation() != null ? parseUuid(r.citation().sourceRefId()) : null;
            String sourceType = r.citation() != null ? r.citation().sourceType() : null;
            String title = r.citation() != null ? r.citation().title() : null;

            items.add(new AiToolResultItem(
                    chunkId,
                    sourceId,
                    sourceType,
                    sourceRefId,
                    null,            // sourceVersionRefId — not exposed here
                    title,
                    List.of(),       // headingPath — not in current CitationResponse
                    safeSnippet,
                    r.score(),
                    rank++,
                    null             // retrievalTraceId — per-response, set at result level
            ));
        }
        return items;
    }

    private String extractRetrievalTraceId(List<RetrievalResultItem> results) {
        // RetrievalTrace is persisted by HybridRetrievalService internally; traceId not in current response.
        // Return null — orchestrator gets it from the service if needed.
        return null;
    }

    private String extractString(Map<String, Object> args, String key) {
        Object val = args.get(key);
        return val instanceof String s ? s : null;
    }

    private Integer extractInt(Map<String, Object> args, String key) {
        Object val = args.get(key);
        if (val instanceof Number n) return n.intValue();
        if (val instanceof String s) {
            try { return Integer.parseInt(s); } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private UUID parseUuid(String value) {
        if (value == null) return null;
        try { return UUID.fromString(value); } catch (IllegalArgumentException e) { return null; }
    }
}
