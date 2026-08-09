package com.company.scopery.modules.specpack.agentsession.application.action;

import com.company.scopery.integration.ai.AiProviderAdapter;
import com.company.scopery.integration.ai.AiProviderAdapterRegistry;
import com.company.scopery.integration.ai.AiProviderRequest;
import com.company.scopery.integration.ai.AiProviderResponse;
import com.company.scopery.modules.specpack.agentsession.application.command.ExecuteStageCommand;
import com.company.scopery.modules.specpack.agentsession.application.response.AgentSessionResponse;
import com.company.scopery.modules.specpack.agentsession.domain.enums.AgentSessionStatus;
import com.company.scopery.modules.specpack.agentsession.domain.enums.AgentStageCode;
import com.company.scopery.modules.specpack.agentsession.domain.enums.AgentStageStatus;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentSession;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentSessionRepository;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentStage;
import com.company.scopery.modules.specpack.block.domain.enums.ChangeSource;
import com.company.scopery.modules.specpack.blockimport.application.service.SpecPackBlockJsonParser;
import com.company.scopery.modules.specpack.blockimport.application.service.SpecPackBlockSchemaValidator;
import com.company.scopery.modules.specpack.blockimport.application.service.SpecPackImportPreviewBuilder;
import com.company.scopery.modules.specpack.blockimport.application.service.SpecPackMergeResolver;
import com.company.scopery.modules.specpack.blockimport.domain.enums.BlockMergeMode;
import com.company.scopery.modules.specpack.blockimport.domain.model.BlockImportItem;
import com.company.scopery.modules.specpack.blockimport.domain.model.ImportPreview;
import com.company.scopery.modules.specpack.outline.domain.enums.OutlineStatus;
import com.company.scopery.modules.specpack.outline.domain.model.SpecPackOutline;
import com.company.scopery.modules.specpack.outline.domain.model.SpecPackOutlineRepository;
import com.company.scopery.modules.specpack.shared.activity.SpecPackActivityLogger;
import com.company.scopery.modules.specpack.shared.constant.SpecPackActivityActions;
import com.company.scopery.modules.specpack.shared.constant.SpecPackEntityTypes;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import com.company.scopery.modules.specpack.shared.util.SpecPackPromptBuilder;
import com.company.scopery.modules.specpack.shared.util.SpecPackScopeLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Executes the GENERATE_CONTENT stage for a SpecPack Agent Session.
 *
 * NOT @Transactional on execute() — calls external AI API which must not hold a DB transaction open.
 * DB writes are committed per-section via a separate @Transactional helper.
 */
@Component
public class ExecuteStageAction {

    private static final Logger log = LoggerFactory.getLogger(ExecuteStageAction.class);
    private static final String PROVIDER_CODE = "OPENAI";
    private static final String MODEL_ID = "gpt-4o";

    @Value("${specpack.ai.max-retries:3}")
    private int maxRetries;

    private final SpecPackAgentSessionRepository sessionRepository;
    private final SpecPackOutlineRepository outlineRepository;
    private final SpecPackScopeLoader scopeLoader;
    private final SpecPackPromptBuilder promptBuilder;
    private final AiProviderAdapterRegistry adapterRegistry;
    private final SpecPackBlockJsonParser jsonParser;
    private final SpecPackBlockSchemaValidator schemaValidator;
    private final SpecPackImportPreviewBuilder previewBuilder;
    private final SpecPackMergeResolver mergeResolver;
    private final SpecPackActivityLogger activityLogger;
    private final ObjectMapper objectMapper;

    public ExecuteStageAction(SpecPackAgentSessionRepository sessionRepository,
                               SpecPackOutlineRepository outlineRepository,
                               SpecPackScopeLoader scopeLoader,
                               SpecPackPromptBuilder promptBuilder,
                               AiProviderAdapterRegistry adapterRegistry,
                               SpecPackBlockJsonParser jsonParser,
                               SpecPackBlockSchemaValidator schemaValidator,
                               SpecPackImportPreviewBuilder previewBuilder,
                               SpecPackMergeResolver mergeResolver,
                               SpecPackActivityLogger activityLogger,
                               ObjectMapper objectMapper) {
        this.sessionRepository = sessionRepository;
        this.outlineRepository = outlineRepository;
        this.scopeLoader = scopeLoader;
        this.promptBuilder = promptBuilder;
        this.adapterRegistry = adapterRegistry;
        this.jsonParser = jsonParser;
        this.schemaValidator = schemaValidator;
        this.previewBuilder = previewBuilder;
        this.mergeResolver = mergeResolver;
        this.activityLogger = activityLogger;
        this.objectMapper = objectMapper;
    }

    // NOT @Transactional — see class-level Javadoc
    public AgentSessionResponse execute(ExecuteStageCommand command) {
        SpecPackAgentSession session = sessionRepository.findById(command.sessionId())
                .filter(s -> s.projectId().equals(command.projectId()))
                .orElseThrow(() -> SpecPackExceptions.agentSessionNotFound(command.sessionId()));

        if (session.status() != AgentSessionStatus.ACTIVE) {
            throw SpecPackExceptions.agentSessionNotActive(command.sessionId());
        }
        if (session.currentStageCode() != AgentStageCode.GENERATE_CONTENT) {
            throw SpecPackExceptions.agentSessionWrongStage(command.sessionId(), AgentStageCode.GENERATE_CONTENT.name());
        }

        SpecPackAgentStage stage = sessionRepository
                .findStageBySessionIdAndCode(command.sessionId(), AgentStageCode.GENERATE_CONTENT.name())
                .orElseThrow(() -> SpecPackExceptions.agentStageNotFound(command.sessionId(), AgentStageCode.GENERATE_CONTENT.name()));

        if (stage.stageStatus() == AgentStageStatus.IN_PROGRESS) {
            throw SpecPackExceptions.agentStageAlreadyInProgress(command.sessionId());
        }

        SpecPackOutline outline = outlineRepository
                .findAllBySessionIdAndStatus(command.sessionId(), OutlineStatus.APPROVED.name())
                .stream().findFirst()
                .orElseThrow(() -> SpecPackExceptions.outlineNotApproved(command.sessionId()));

        markStageInProgress(stage);

        SpecPackScopeLoader.ScopeContext scope = scopeLoader.load(
                command.sessionId(), session.projectId(), session.scopePackageId());

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> sections = (List<Map<String, Object>>) outline.outlineJson().get("sections");
        if (sections == null || sections.isEmpty()) {
            log.warn("[ExecuteStage] No sections in approved outline for session={}", command.sessionId());
            return completeStage(stage, session, 0);
        }

        AiProviderAdapter adapter = adapterRegistry.getAdapter(PROVIDER_CODE);
        int successCount = 0;

        for (int i = 0; i < sections.size(); i++) {
            Map<String, Object> section = sections.get(i);
            String sectionTitle = section.getOrDefault("title", "Section " + (i + 1)).toString();

            boolean sectionSuccess = false;
            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                try {
                    String previousBlocksJson = buildPreviousBlocksJson(sections.subList(Math.max(0, i - 2), i));
                    SpecPackPromptBuilder.SectionPrompt sectionPrompt =
                            promptBuilder.buildSectionPrompt(scope, section, previousBlocksJson);

                    String fullPrompt = sectionPrompt.systemPrompt().isBlank()
                            ? sectionPrompt.userPrompt()
                            : sectionPrompt.systemPrompt() + "\n\n" + sectionPrompt.userPrompt();

                    AiProviderResponse response = adapter.call(new AiProviderRequest(
                            null, MODEL_ID, fullPrompt,
                            BigDecimal.valueOf(0.3),
                            sectionPrompt.maxTokens()
                    ));

                    String rawOutput = response.outputText() != null ? response.outputText().strip() : "";
                    applyAiOutput(rawOutput, session.specPackId());
                    sectionSuccess = true;
                    successCount++;
                    log.info("[ExecuteStage] section='{}' applied (attempt {}/{})", sectionTitle, attempt, maxRetries);
                    break;

                } catch (Exception e) {
                    log.warn("[ExecuteStage] section='{}' attempt {}/{} failed: {}",
                            sectionTitle, attempt, maxRetries, e.getMessage());
                    if (attempt == maxRetries) {
                        log.error("[ExecuteStage] All retries exhausted for section='{}' in session={}",
                                sectionTitle, command.sessionId());
                    }
                }
            }

            if (!sectionSuccess) {
                log.warn("[ExecuteStage] Skipping section='{}' after {} failed attempts", sectionTitle, maxRetries);
            }
        }

        return completeStage(stage, session, successCount);
    }

    @Transactional
    protected void applyAiOutput(String rawOutput, UUID specPackId) {
        byte[] payload = extractJson(rawOutput).getBytes(StandardCharsets.UTF_8);
        SpecPackBlockJsonParser.ParseResult parsed = jsonParser.parse(payload);
        List<BlockImportItem> validated = schemaValidator.validate(parsed.items());
        ImportPreview preview = previewBuilder.build(parsed.schemaVersion(), validated, specPackId, BlockMergeMode.MERGE_BY_BLOCK_KEY);
        mergeResolver.apply(preview.items(), specPackId, ChangeSource.EXTERNAL_AGENT);
    }

    @Transactional
    protected void markStageInProgress(SpecPackAgentStage stage) {
        stage.start();
        sessionRepository.saveStage(stage);
    }

    @Transactional
    protected AgentSessionResponse completeStage(SpecPackAgentStage stage, SpecPackAgentSession session, int sectionsApplied) {
        stage.complete(Map.of("sectionsApplied", sectionsApplied));
        sessionRepository.saveStage(stage);

        session.advanceToStage(AgentStageCode.COMPLETED);
        session.complete();
        SpecPackAgentSession saved = sessionRepository.save(session);

        activityLogger.logSuccess(
                SpecPackEntityTypes.SPEC_PACK_AGENT_SESSION,
                saved.id(),
                SpecPackActivityActions.GENERATE_CONTENT_COMPLETED,
                "GENERATE_CONTENT completed — " + sectionsApplied + " sections applied for session " + saved.id()
        );

        return AgentSessionResponse.from(saved);
    }

    private String buildPreviousBlocksJson(List<Map<String, Object>> previousSections) {
        try {
            return objectMapper.writeValueAsString(previousSections.stream()
                    .map(s -> Map.of("title", s.getOrDefault("title", "")))
                    .toList());
        } catch (Exception e) {
            return "[]";
        }
    }

    private String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) return text.substring(start, end + 1);
        return text;
    }
}
