package com.company.scopery.modules.aiassistant.application.orchestrator;

import com.company.scopery.integration.ai.AiChatMessage;
import com.company.scopery.integration.ai.AiStreamingProviderAdapterRegistry;
import com.company.scopery.integration.ai.AiStreamingRequest;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolExecutionContext;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolHandlerRegistry;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolResult;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolResultItem;
import com.company.scopery.modules.aiassistant.application.port.AiAssistantCitationValidationService;
import com.company.scopery.modules.aiassistant.application.port.AiAssistantCitationValidationService.ValidatedCitation;
import com.company.scopery.modules.aiassistant.application.port.AiAssistantContextBuilder;
import com.company.scopery.modules.aiassistant.application.port.AiAssistantContextBuilder.AiResolvedContext;
import com.company.scopery.modules.aiassistant.application.service.AiAssistantMemorySummaryService;
import com.company.scopery.modules.aiassistant.application.service.AiAssistantQuotaService;
import com.company.scopery.modules.aiassistant.domain.enums.ConversationType;
import com.company.scopery.modules.aiassistant.domain.enums.MessageRole;
import com.company.scopery.modules.aiassistant.domain.enums.ResponseMode;
import com.company.scopery.modules.aiassistant.domain.model.AiConversation;
import com.company.scopery.modules.aiassistant.domain.model.AiConversationRepository;
import com.company.scopery.modules.aiassistant.domain.model.AiMessage;
import com.company.scopery.modules.aiassistant.domain.model.AiMessageCitation;
import com.company.scopery.modules.aiassistant.domain.model.AiMessageCitationRepository;
import com.company.scopery.modules.aiassistant.domain.model.AiMessageRepository;
import com.company.scopery.modules.aiassistant.domain.model.AiToolCallRecord;
import com.company.scopery.modules.aiassistant.domain.model.AiToolCallRepository;
import com.company.scopery.modules.aiassistant.infrastructure.sse.AiAssistantSseStreamService;
import com.company.scopery.modules.aiassistant.shared.activity.AiAssistantActivityLogger;
import com.company.scopery.modules.aiassistant.shared.config.AiAssistantProperties;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantActivityActions;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantEntityTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Orchestrates a single AI assistant turn (user question -> streamed answer).
 *
 * IMPORTANT: executeTurn() is intentionally NOT @Transactional. The method calls an external
 * AI provider over HTTP (which can take seconds) and must not hold a DB connection/transaction open.
 * Execution status must also be persisted in its own committed transaction even when the provider
 * call throws midway, so a failed turn is recorded instead of being rolled back.
 * Per CLAUDE.md section 21 — same sanctioned exception as Phase 10 execution actions.
 */
@Component
public class AiAssistantTurnOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(AiAssistantTurnOrchestrator.class);

    private static final String TOOL_CODE_KNOWLEDGE_SEARCH = "knowledge.search";
    private static final int MAX_STREAM_SEQUENCE = 4096;

    private final AiMessageRepository messageRepository;
    private final AiConversationRepository conversationRepository;
    private final AiToolCallRepository toolCallRepository;
    private final AiMessageCitationRepository citationRepository;
    private final AiAssistantContextBuilder contextBuilder;
    private final AiAssistantCitationValidationService citationValidationService;
    private final AiAssistantQuotaService quotaService;
    private final AiAssistantMemorySummaryService memorySummaryService;
    private final AiAssistantSseStreamService sseStreamService;
    private final AiToolHandlerRegistry toolHandlerRegistry;
    private final AiStreamingProviderAdapterRegistry streamingProviderRegistry;
    private final AiAssistantProperties properties;
    private final AiAssistantActivityLogger activityLogger;
    private final TransactionTemplate transactionTemplate;

    public AiAssistantTurnOrchestrator(AiMessageRepository messageRepository,
                                       AiConversationRepository conversationRepository,
                                       AiToolCallRepository toolCallRepository,
                                       AiMessageCitationRepository citationRepository,
                                       AiAssistantContextBuilder contextBuilder,
                                       AiAssistantCitationValidationService citationValidationService,
                                       AiAssistantQuotaService quotaService,
                                       AiAssistantMemorySummaryService memorySummaryService,
                                       AiAssistantSseStreamService sseStreamService,
                                       AiToolHandlerRegistry toolHandlerRegistry,
                                       AiStreamingProviderAdapterRegistry streamingProviderRegistry,
                                       AiAssistantProperties properties,
                                       AiAssistantActivityLogger activityLogger,
                                       PlatformTransactionManager transactionManager) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.toolCallRepository = toolCallRepository;
        this.citationRepository = citationRepository;
        this.contextBuilder = contextBuilder;
        this.citationValidationService = citationValidationService;
        this.quotaService = quotaService;
        this.memorySummaryService = memorySummaryService;
        this.sseStreamService = sseStreamService;
        this.toolHandlerRegistry = toolHandlerRegistry;
        this.streamingProviderRegistry = streamingProviderRegistry;
        this.properties = properties;
        this.activityLogger = activityLogger;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public record TurnRequest(
            UUID conversationId,
            UUID assistantMessageId,
            UUID userMessageId,
            UUID turnId,
            UUID actorId,
            UUID workspaceId,
            UUID projectId,
            String userQuestion,
            String promptProfileCode,
            UUID modelDeploymentId,
            String modelProvider,
            String modelName
    ) {}

    // NOT @Transactional — see class-level Javadoc
    public void executeTurn(TurnRequest req) {
        Instant turnStarted = Instant.now();
        final long[] seqHolder = {0};

        try {
            // A. Mark CONTEXTUALIZING
            transactionTemplate.execute(status -> {
                AiMessage msg = findMessageOrThrow(req.assistantMessageId());
                msg.markContextualizing();
                messageRepository.save(msg);
                return null;
            });

            // B. Emit message.started
            seqHolder[0]++;
            persistAndEmit(req.assistantMessageId(), seqHolder[0], "message.started",
                    Map.of("messageId", req.assistantMessageId().toString()));

            // C. Build context (has its own @Transactional)
            AiResolvedContext resolvedContext = contextBuilder.build(
                    req.conversationId(), req.assistantMessageId(), req.turnId(),
                    req.actorId(), req.workspaceId(), req.projectId());

            // D. Emit context.completed
            seqHolder[0]++;
            persistAndEmit(req.assistantMessageId(), seqHolder[0], "context.completed",
                    Map.of("contextHash", resolvedContext.contextHash()));

            // E. Check cancelled
            if (isCancelRequested(req.assistantMessageId())) {
                markCancelled(req.assistantMessageId());
                persistAndEmit(req.assistantMessageId(), seqHolder[0] + 1, "answer.cancelled", Map.of());
                return;
            }

            // F. Determine responseMode
            AiConversation conversation = conversationRepository.findById(req.conversationId()).orElse(null);
            boolean isProjectAssistant = conversation != null
                    && conversation.conversationType() == ConversationType.PROJECT_ASSISTANT;
            ResponseMode responseMode = isProjectAssistant
                    ? ResponseMode.GROUNDED_ANSWER
                    : ResponseMode.GENERAL_GUIDE;

            // Tool result tracking
            AiToolResult toolResult = null;
            final UUID[] toolCallRecordIdRef = {null};

            // G. GROUNDED_ANSWER path — tool retrieval
            if (responseMode == ResponseMode.GROUNDED_ANSWER) {
                // G1. Persist TOOL_REQUEST message + AiToolCallRecord
                transactionTemplate.execute(status -> {
                    int currentCount = messageRepository.countByConversationId(req.conversationId());
                    AiMessage toolReqMsg = AiMessage.createToolRequest(
                            req.conversationId(), req.turnId(), currentCount + 1,
                            "{\"toolCode\":\"" + TOOL_CODE_KNOWLEDGE_SEARCH + "\","
                                    + "\"query\":\"[REDACTED]\"}",
                            null);
                    AiMessage savedReqMsg = messageRepository.save(toolReqMsg);

                    AiToolCallRecord callRecord = AiToolCallRecord.request(
                            req.conversationId(), req.turnId(), savedReqMsg.id(),
                            TOOL_CODE_KNOWLEDGE_SEARCH, "1.0", TOOL_CODE_KNOWLEDGE_SEARCH,
                            String.valueOf(req.userQuestion().hashCode()),
                            "{\"query\":\"[REDACTED]\"}",
                            "{\"workspaceId\":\"" + req.workspaceId() + "\"}");
                    AiToolCallRecord savedRecord = toolCallRepository.save(callRecord);
                    toolCallRecordIdRef[0] = savedRecord.id();
                    return null;
                });

                // G2. Emit retrieval.started
                seqHolder[0]++;
                persistAndEmit(req.assistantMessageId(), seqHolder[0], "retrieval.started", Map.of());

                // G3. Check cancelled
                if (isCancelRequested(req.assistantMessageId())) {
                    markCancelled(req.assistantMessageId());
                    persistAndEmit(req.assistantMessageId(), seqHolder[0] + 1, "answer.cancelled", Map.of());
                    return;
                }

                // G4. Dispatch tool
                Map<String, Object> toolArgs = Map.of(
                        "query", req.userQuestion(),
                        "topK", properties.getMaxEvidenceChunks());
                AiToolExecutionContext toolCtx = new AiToolExecutionContext(
                        req.actorId(), req.workspaceId(), req.projectId(),
                        List.of(), null, null, null);

                toolResult = toolHandlerRegistry.dispatch(TOOL_CODE_KNOWLEDGE_SEARCH, toolArgs, toolCtx);

                // G5. Persist TOOL_RESULT message + update AiToolCallRecord
                final AiToolResult toolResultCopy = toolResult;
                transactionTemplate.execute(status -> {
                    int currentCount = messageRepository.countByConversationId(req.conversationId());
                    String resultJson = toolResultCopy.success()
                            ? "{\"resultCount\":" + toolResultCopy.resultCount()
                            + ",\"truncated\":" + toolResultCopy.truncated() + "}"
                            : "{\"errorCode\":\"" + toolResultCopy.errorCode() + "\"}";

                    AiMessage toolResMsg = AiMessage.createToolResult(
                            req.conversationId(), req.turnId(), currentCount + 1, resultJson, null);
                    AiMessage savedResMsg = messageRepository.save(toolResMsg);

                    if (toolCallRecordIdRef[0] != null) {
                        toolCallRepository.findById(toolCallRecordIdRef[0]).ifPresent(record -> {
                            if (toolResultCopy.success()) {
                                record.markSucceeded(
                                        savedResMsg.id(),
                                        toolResultCopy.retrievalTraceId() != null
                                                ? tryParseUuid(toolResultCopy.retrievalTraceId()) : null,
                                        toolResultCopy.resultCount(),
                                        toolResultCopy.truncated(),
                                        resultJson,
                                        null);
                            } else {
                                record.markFailed(toolResultCopy.errorCode(),
                                        toolResultCopy.errorSummary(), null);
                            }
                            toolCallRepository.save(record);
                        });
                    }
                    return null;
                });

                // G6. Emit retrieval.completed
                seqHolder[0]++;
                persistAndEmit(req.assistantMessageId(), seqHolder[0], "retrieval.completed",
                        Map.of("resultCount", toolResult.resultCount(), "truncated", toolResult.truncated()));
            }

            // H. Check cancelled
            if (isCancelRequested(req.assistantMessageId())) {
                markCancelled(req.assistantMessageId());
                persistAndEmit(req.assistantMessageId(), seqHolder[0] + 1, "answer.cancelled", Map.of());
                return;
            }

            // I. Assemble prompt
            List<AiMessage> recentMessages = messageRepository.findByConversationIdAndRoleInOrderBySequenceDesc(
                    req.conversationId(),
                    List.of(MessageRole.USER, MessageRole.ASSISTANT),
                    properties.getMemory().getKeepLatestMessages());

            List<AiChatMessage> chatMessages = new ArrayList<>();
            chatMessages.add(new AiChatMessage("system", "You are Scopery AI Assistant."));

            List<AiMessage> priorMessages = new ArrayList<>(recentMessages);
            Collections.reverse(priorMessages);
            for (AiMessage prior : priorMessages) {
                if (prior.id().equals(req.userMessageId())) continue;
                String role = prior.role() == MessageRole.ASSISTANT ? "assistant" : "user";
                if (prior.content() != null && !prior.content().isBlank()) {
                    chatMessages.add(new AiChatMessage(role, prior.content()));
                }
            }

            chatMessages.add(new AiChatMessage("user", req.userQuestion()));

            if (toolResult != null && toolResult.success() && !toolResult.items().isEmpty()) {
                StringBuilder evidenceBuilder = new StringBuilder("[Evidence]\n");
                int evidenceIndex = 1;
                for (AiToolResultItem item : toolResult.items()) {
                    evidenceBuilder.append(evidenceIndex++).append(". ").append(item.title()).append("\n");
                    if (item.safeSnippet() != null) {
                        evidenceBuilder.append(item.safeSnippet()).append("\n");
                    }
                    evidenceBuilder.append("\n");
                }
                chatMessages.add(new AiChatMessage("user", evidenceBuilder.toString()));
            }

            // J. Check streaming provider availability
            try {
                streamingProviderRegistry.getAdapter(req.modelProvider());
            } catch (Exception e) {
                log.warn("[AiAssistantTurnOrchestrator] No streaming provider for provider={}: {}",
                        req.modelProvider(), e.getMessage());
                markFailed(req.assistantMessageId(), "AI_ASSISTANT_MODEL_UNAVAILABLE",
                        "No streaming provider adapter available: " + req.modelProvider());
                persistAndEmit(req.assistantMessageId(), seqHolder[0] + 1, "answer.failed",
                        Map.of("errorCode", "AI_ASSISTANT_MODEL_UNAVAILABLE"));
                return;
            }

            // K. Stream via provider
            AiStreamingRequest streamingRequest = new AiStreamingRequest(
                    null, req.modelName(), chatMessages,
                    new BigDecimal("0.7"), properties.getMaxOutputTokens());

            StringBuilder contentBuilder = new StringBuilder();
            final int[] inputTokensHolder = {0};
            final int[] outputTokensHolder = {0};
            final String[] finishReasonHolder = {null};
            final boolean[] streamLimitExceeded = {false};
            final boolean[] cancelledDuringStream = {false};

            streamingProviderRegistry.getAdapter(req.modelProvider()).streamChat(streamingRequest,
                    (textDelta, isLast, finishReason, inputTokens, outputTokens) -> {
                        if (isCancelRequested(req.assistantMessageId())) {
                            cancelledDuringStream[0] = true;
                            return;
                        }
                        if (cancelledDuringStream[0] || streamLimitExceeded[0]) {
                            return;
                        }

                        if (textDelta != null) {
                            contentBuilder.append(textDelta);
                        }
                        seqHolder[0]++;

                        if (seqHolder[0] > MAX_STREAM_SEQUENCE) {
                            streamLimitExceeded[0] = true;
                            return;
                        }

                        Instant expiry = Instant.now().plus(properties.getStreamEventRetention());
                        sseStreamService.persistAndEmit(req.assistantMessageId(), seqHolder[0],
                                "answer.delta",
                                Map.of("delta", textDelta != null ? textDelta : ""),
                                expiry);

                        if (isLast) {
                            finishReasonHolder[0] = finishReason;
                            if (inputTokens != null) inputTokensHolder[0] = inputTokens;
                            if (outputTokens != null) outputTokensHolder[0] = outputTokens;
                        }
                    });

            if (streamLimitExceeded[0]) {
                markFailed(req.assistantMessageId(), "AI_STREAM_EVENT_LIMIT_EXCEEDED",
                        "Stream event limit of " + MAX_STREAM_SEQUENCE + " exceeded for message: "
                                + req.assistantMessageId());
                persistAndEmit(req.assistantMessageId(), seqHolder[0] + 1, "answer.failed",
                        Map.of("errorCode", "AI_STREAM_EVENT_LIMIT_EXCEEDED"));
                return;
            }

            if (cancelledDuringStream[0]) {
                markCancelled(req.assistantMessageId());
                persistAndEmit(req.assistantMessageId(), seqHolder[0] + 1, "answer.cancelled", Map.of());
                return;
            }

            // L. Validate citations
            final AiToolResult finalToolResult = toolResult;
            List<ValidatedCitation> validatedCitations = List.of();
            if (finalToolResult != null && finalToolResult.success() && !finalToolResult.items().isEmpty()) {
                List<String> allChunkIds = finalToolResult.items().stream()
                        .filter(item -> item.chunkId() != null)
                        .map(item -> item.chunkId().toString())
                        .toList();
                validatedCitations = citationValidationService.validate(finalToolResult.items(), allChunkIds);
            }

            // M. Finalize: mark completed + save citations + touch conversation
            final String finalContent = contentBuilder.toString();
            final int finalInputTokens = inputTokensHolder[0];
            final int finalOutputTokens = outputTokensHolder[0];
            final String finalFinishReason = finishReasonHolder[0];
            final ResponseMode finalResponseMode = responseMode;
            final int latencyMs = (int) (Instant.now().toEpochMilli() - turnStarted.toEpochMilli());
            final List<ValidatedCitation> finalCitations = validatedCitations;

            transactionTemplate.execute(status -> {
                AiMessage assistantMsg = findMessageOrThrow(req.assistantMessageId());
                assistantMsg.markCompleted(
                        finalContent, finalResponseMode,
                        req.modelProvider(), req.modelName(),
                        req.modelDeploymentId() != null ? req.modelDeploymentId().toString() : null,
                        req.promptProfileCode(),
                        finalInputTokens, finalOutputTokens, latencyMs, finalFinishReason);
                messageRepository.save(assistantMsg);

                if (!finalCitations.isEmpty()) {
                    UUID retrievalTraceId = (finalToolResult != null && finalToolResult.retrievalTraceId() != null)
                            ? tryParseUuid(finalToolResult.retrievalTraceId()) : null;
                    List<AiMessageCitation> domainCitations = new ArrayList<>();
                    int ordinal = 1;
                    for (ValidatedCitation vc : finalCitations) {
                        AiMessageCitation citation = AiMessageCitation.create(
                                req.assistantMessageId(), ordinal++,
                                retrievalTraceId,
                                vc.chunkId(),
                                vc.sourceType(),
                                vc.sourceRefId(),
                                vc.sourceVersionRefId(),
                                vc.title(),
                                vc.headingPath(),
                                vc.quotedFragment(),
                                null,
                                vc.accessValidationResult());
                        domainCitations.add(citation);
                    }
                    citationRepository.saveAll(domainCitations);
                }

                conversationRepository.findById(req.conversationId()).ifPresent(conv -> {
                    conv.touchLastMessage(Instant.now());
                    conversationRepository.save(conv);
                });
                return null;
            });

            // N. Emit answer.completed
            seqHolder[0]++;
            persistAndEmit(req.assistantMessageId(), seqHolder[0], "answer.completed",
                    Map.of("inputTokens", finalInputTokens,
                            "outputTokens", finalOutputTokens,
                            "finishReason", finalFinishReason != null ? finalFinishReason : "stop"));

            // O. Emit citation.added for each citation
            int citationOrdinal = 1;
            for (ValidatedCitation vc : validatedCitations) {
                seqHolder[0]++;
                persistAndEmit(req.assistantMessageId(), seqHolder[0], "citation.added",
                        Map.of("ordinal", citationOrdinal++,
                                "title", vc.title() != null ? vc.title() : "",
                                "sourceType", vc.sourceType() != null ? vc.sourceType() : ""));
            }

            // P. Finalize quota
            quotaService.finalizeQuota(req.actorId(), req.workspaceId(),
                    finalInputTokens, finalOutputTokens, false, false);

            // Q. Check memory summarization
            int totalMessageCount = messageRepository.countByConversationId(req.conversationId());
            if (memorySummaryService.needsSummarization(req.conversationId(), totalMessageCount)) {
                log.info("[AiAssistantTurnOrchestrator] Memory summarization triggered for " +
                                "conversationId={} at turnCount={} — async summarization deferred.",
                        req.conversationId(), totalMessageCount);
            }

            activityLogger.logSuccess(AiAssistantEntityTypes.AI_MESSAGE, req.assistantMessageId(),
                    AiAssistantActivityActions.SEND_AI_MESSAGE,
                    "AI assistant turn completed for conversationId=" + req.conversationId());

        } catch (Exception ex) {
            log.error("[AiAssistantTurnOrchestrator] Turn failed for messageId={}: {}",
                    req.assistantMessageId(), ex.getMessage(), ex);
            try {
                markFailed(req.assistantMessageId(), "AI_ASSISTANT_TURN_FAILED",
                        "Unexpected error during turn execution");
                persistAndEmit(req.assistantMessageId(), seqHolder[0] + 1, "answer.failed",
                        Map.of("errorCode", "AI_ASSISTANT_TURN_FAILED"));
                quotaService.finalizeQuota(req.actorId(), req.workspaceId(), 0, 0, true, false);
            } catch (Exception inner) {
                log.error("[AiAssistantTurnOrchestrator] Failed to record failure state for messageId={}",
                        req.assistantMessageId(), inner);
            }
        }
    }

    // --- Private helper methods ---

    private AiMessage findMessageOrThrow(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalStateException(
                        "Assistant message not found during turn execution: " + messageId));
    }

    private boolean isCancelRequested(UUID messageId) {
        return messageRepository.findById(messageId)
                .map(AiMessage::isCancelRequested)
                .orElse(false);
    }

    private void markFailed(UUID messageId, String errorCode, String summary) {
        transactionTemplate.execute(status -> {
            messageRepository.findById(messageId).ifPresent(msg -> {
                if (!msg.isFinal()) {
                    msg.markFailed(errorCode, summary);
                    messageRepository.save(msg);
                }
            });
            return null;
        });
    }

    private void markCancelled(UUID messageId) {
        transactionTemplate.execute(status -> {
            messageRepository.findById(messageId).ifPresent(msg -> {
                if (!msg.isFinal()) {
                    msg.markCancelled();
                    messageRepository.save(msg);
                }
            });
            return null;
        });
    }

    private void persistAndEmit(UUID messageId, long sequence, String eventType, Map<String, Object> payload) {
        Instant expiresAt = Instant.now().plus(properties.getStreamEventRetention());
        sseStreamService.persistAndEmit(messageId, sequence, eventType, payload, expiresAt);
    }

    private static UUID tryParseUuid(String value) {
        if (value == null) return null;
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
