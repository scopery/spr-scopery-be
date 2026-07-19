package com.company.scopery.modules.aiagent.execution.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.integration.ai.AiProviderAdapter;
import com.company.scopery.integration.ai.AiProviderAdapterRegistry;
import com.company.scopery.integration.ai.AiProviderRequest;
import com.company.scopery.integration.ai.AiProviderResponse;
import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentStatus;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.aimodel.domain.enums.AiModelStatus;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentStatus;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventConfigStatus;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfig;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfigRepository;
import com.company.scopery.modules.aiagent.execution.application.command.ExecuteEventConfigCommand;
import com.company.scopery.modules.aiagent.execution.application.prompt.PromptRenderer;
import com.company.scopery.modules.aiagent.execution.application.response.ExecutionRunResponse;
import com.company.scopery.modules.aiagent.execution.application.response.UsagePolicyWarningInfo;
import com.company.scopery.modules.aiagent.execution.application.service.AiExecutionSchemaValidator;
import com.company.scopery.modules.aiagent.execution.domain.enums.ExecutionTriggerSource;
import com.company.scopery.modules.aiagent.execution.domain.model.ExecutionLog;
import com.company.scopery.modules.aiagent.execution.domain.model.ExecutionLogRepository;
import com.company.scopery.modules.aiagent.execution.domain.valueobject.ExecutionRequestId;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplate;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplateRepository;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptTemplateStatus;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersionRepository;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptVersionStatus;
import com.company.scopery.modules.aiagent.provider.domain.model.Provider;
import com.company.scopery.modules.aiagent.provider.domain.model.ProviderRepository;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderStatus;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import com.company.scopery.modules.aiagent.usagepolicy.application.evaluator.UsagePolicyEvaluationContext;
import com.company.scopery.modules.aiagent.usagepolicy.application.evaluator.UsagePolicyEvaluationResult;
import com.company.scopery.modules.aiagent.usagepolicy.application.evaluator.UsagePolicyEvaluator;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDefinitionStatus;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class ExecuteEventConfigAction {

    private static final String EVENT_USAGE_POLICY_BLOCKED = "AI_USAGE_POLICY_BLOCKED";

    private final EventConfigRepository eventConfigRepository;
    private final EventDefinitionRepository eventDefinitionRepository;
    private final AgentRepository agentRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final PromptTemplateRepository promptTemplateRepository;
    private final ModelDeploymentRepository modelDeploymentRepository;
    private final AiModelRepository aiModelRepository;
    private final ProviderRepository providerRepository;
    private final ExecutionLogRepository executionLogRepository;
    private final PromptRenderer promptRenderer;
    private final AiProviderAdapterRegistry adapterRegistry;
    private final AiAgentActivityLogger activityLogger;
    private final UsagePolicyEvaluator usagePolicyEvaluator;
    private final AiExecutionSchemaValidator schemaValidator;
    private final TransactionalOutboxService outboxService;

    public ExecuteEventConfigAction(EventConfigRepository eventConfigRepository,
                                     EventDefinitionRepository eventDefinitionRepository,
                                     AgentRepository agentRepository,
                                     PromptVersionRepository promptVersionRepository,
                                     PromptTemplateRepository promptTemplateRepository,
                                     ModelDeploymentRepository modelDeploymentRepository,
                                     AiModelRepository aiModelRepository,
                                     ProviderRepository providerRepository,
                                     ExecutionLogRepository executionLogRepository,
                                     PromptRenderer promptRenderer,
                                     AiProviderAdapterRegistry adapterRegistry,
                                     AiAgentActivityLogger activityLogger,
                                     UsagePolicyEvaluator usagePolicyEvaluator,
                                     AiExecutionSchemaValidator schemaValidator,
                                     TransactionalOutboxService outboxService) {
        this.eventConfigRepository = eventConfigRepository;
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.agentRepository = agentRepository;
        this.promptVersionRepository = promptVersionRepository;
        this.promptTemplateRepository = promptTemplateRepository;
        this.modelDeploymentRepository = modelDeploymentRepository;
        this.aiModelRepository = aiModelRepository;
        this.providerRepository = providerRepository;
        this.executionLogRepository = executionLogRepository;
        this.promptRenderer = promptRenderer;
        this.adapterRegistry = adapterRegistry;
        this.activityLogger = activityLogger;
        this.usagePolicyEvaluator = usagePolicyEvaluator;
        this.schemaValidator = schemaValidator;
        this.outboxService = outboxService;
    }

    // CLAUDE.md §21 normally requires @Transactional on every Action.execute(). This is a
    // documented exception: the AI provider call inside orchestrate() can take seconds and must
    // not hold a DB transaction open, and execution status (RUNNING -> SUCCEEDED/FAILED) must be
    // persisted in its own committed transaction even when the provider call throws midway, so a
    // failed run is still recorded instead of being rolled back with the rest of the method.
    public ExecutionRunResponse execute(ExecuteEventConfigCommand command) {
        EventConfig eventConfig = eventConfigRepository.findById(command.eventConfigId())
                .orElseThrow(() -> AiAgentExceptions.executionEventConfigNotFoundById(command.eventConfigId()));

        if (eventConfig.status() != EventConfigStatus.ACTIVE) {
            throw AiAgentExceptions.executionEventConfigNotActive(eventConfig.id());
        }

        ExecutionTriggerSource triggerSource = resolveTriggerSource(
                command.triggerSource(), ExecutionTriggerSource.MANUAL);

        return orchestrate(eventConfig, command.requestId(), command.inputVariables(),
                triggerSource, AiAgentActivityActions.EXECUTE_EVENT_CONFIG);
    }

    private ExecutionTriggerSource resolveTriggerSource(String raw, ExecutionTriggerSource defaultSource) {
        ExecutionTriggerSource parsed = AiAgentEnumParser.parseOptional(
                ExecutionTriggerSource.class, raw,
                AiAgentErrorCatalog.INVALID_EXECUTION_TRIGGER_SOURCE.code(), "triggerSource");
        return parsed != null ? parsed : defaultSource;
    }

    private ExecutionRunResponse orchestrate(EventConfig eventConfig, String requestId,
                                              java.util.Map<String, String> inputVariables,
                                              ExecutionTriggerSource triggerSource,
                                              String activityAction) {
        String resolvedRequestId = (requestId != null && !requestId.isBlank())
                ? requestId.trim() : UUID.randomUUID().toString();

        ExecutionRequestId execRequestId = ExecutionRequestId.of(resolvedRequestId);
        if (executionLogRepository.existsByRequestId(execRequestId)) {
            throw AiAgentExceptions.executionLogRequestIdAlreadyExists(resolvedRequestId);
        }

        EventDefinition eventDef = eventDefinitionRepository.findById(eventConfig.eventDefinitionId())
                .orElseThrow(() -> AiAgentExceptions.executionEventDefinitionNotFound(
                        eventConfig.eventDefinitionId().toString()));
        if (eventDef.status() != EventDefinitionStatus.ACTIVE) {
            throw AiAgentExceptions.executionEventDefinitionNotActive(eventDef.id());
        }

        Agent agent = agentRepository.findById(eventConfig.agentId())
                .orElseThrow(() -> AiAgentExceptions.executionAgentNotFound(eventConfig.agentId()));
        if (agent.status() != AgentStatus.ACTIVE) {
            throw AiAgentExceptions.executionAgentNotActive(agent.id());
        }

        PromptVersion promptVersion = promptVersionRepository.findById(eventConfig.promptVersionId())
                .orElseThrow(() -> AiAgentExceptions.executionPromptVersionNotFound(eventConfig.promptVersionId()));
        if (promptVersion.status() != PromptVersionStatus.ACTIVE) {
            throw AiAgentExceptions.executionPromptVersionNotActive(promptVersion.id());
        }

        PromptTemplate promptTemplate = promptTemplateRepository.findById(promptVersion.templateId())
                .orElseThrow(() -> AiAgentExceptions.executionPromptTemplateNotFound(promptVersion.templateId()));
        if (promptTemplate.status() != PromptTemplateStatus.ACTIVE) {
            throw AiAgentExceptions.executionPromptTemplateNotActive(promptTemplate.id());
        }

        if (!promptTemplate.agentId().equals(eventConfig.agentId())) {
            throw AiAgentExceptions.executionPromptTemplateAgentMismatch(
                    promptTemplate.id(), eventConfig.agentId());
        }

        ModelDeployment deployment = modelDeploymentRepository.findById(eventConfig.modelDeploymentId())
                .orElseThrow(() -> AiAgentExceptions.executionModelDeploymentNotFound(eventConfig.modelDeploymentId()));
        if (deployment.status() != ModelDeploymentStatus.ACTIVE) {
            throw AiAgentExceptions.executionModelDeploymentNotActive(deployment.id());
        }

        if (!deployment.environment().name().equals(eventConfig.environment().name())) {
            throw AiAgentExceptions.executionModelDeploymentEnvironmentMismatch(
                    deployment.environment().name(), eventConfig.environment().name());
        }

        AiModel aiModel = aiModelRepository.findById(deployment.modelId())
                .orElseThrow(() -> AiAgentExceptions.executionAiModelNotFound(deployment.modelId()));
        if (aiModel.status() != AiModelStatus.ACTIVE) {
            throw AiAgentExceptions.executionAiModelNotActive(aiModel.id());
        }

        Provider provider = providerRepository.findById(aiModel.providerId())
                .orElseThrow(() -> AiAgentExceptions.executionProviderNotFound(aiModel.providerId()));
        if (provider.status() != ProviderStatus.ACTIVE) {
            throw AiAgentExceptions.executionProviderNotActive(provider.id());
        }

        schemaValidator.validateInput(eventDef.inputSchema(), promptVersion.variableSchema(),
                promptVersion.resolvedPromptContent(), inputVariables);

        String traceId = MDC.get("traceId");
        UsagePolicyEvaluationContext policyContext = new UsagePolicyEvaluationContext(
                eventConfig.id(), eventConfig.eventDefinitionId(), agent.id(), deployment.id(),
                promptVersion.id(), provider.id(), resolvedRequestId, triggerSource.name(), Instant.now(),
                eventConfig.environment().name(), null);
        UsagePolicyEvaluationResult policyResult = usagePolicyEvaluator.evaluate(policyContext);
        if (policyResult.isBlocked()) {
            String blockReason = policyResult.violations().isEmpty()
                    ? "USAGE_POLICY_EXCEEDED"
                    : policyResult.violations().get(0).metricName();
            String policyCode = policyResult.violations().isEmpty()
                    ? "" : policyResult.violations().get(0).policyCode();

            ExecutionLog blockedLog = ExecutionLog.create(
                    execRequestId, eventConfig.id(), eventConfig.eventDefinitionId(),
                    agent.id(), promptVersion.id(), promptTemplate.id(),
                    deployment.id(), provider.id(), aiModel.id(),
                    eventConfig.environment().name(), null, null, null, traceId,
                    triggerSource, null);
            blockedLog.markBlocked(blockReason);
            executionLogRepository.save(blockedLog);

            outboxService.enqueue("AI_EXECUTION_LOG", blockedLog.id(), EVENT_USAGE_POLICY_BLOCKED,
                    "SCOPERY_AI_AGENT", 1, Map.of(
                            "executionLogId", blockedLog.id().toString(),
                            "requestId", resolvedRequestId,
                            "eventConfigId", eventConfig.id().toString(),
                            "policyCode", policyCode,
                            "blockReasonCode", blockReason,
                            "traceId", traceId == null ? "" : traceId
                    ));

            activityLogger.logSuccess(AiAgentEntityTypes.EXECUTION_LOG, blockedLog.id(),
                    AiAgentActivityActions.EXECUTE_EVENT_CONFIG,
                    "Execution blocked by usage policy for requestId: " + resolvedRequestId);

            throw AiAgentExceptions.executionPolicyBlocked(
                    policyCode == null || policyCode.isBlank() ? blockReason : policyCode);
        }

        String renderedPrompt = promptRenderer.render(promptVersion.resolvedPromptContent(), inputVariables);

        ExecutionLog log = ExecutionLog.create(
                execRequestId, eventConfig.id(), eventConfig.eventDefinitionId(),
                agent.id(), promptVersion.id(), promptTemplate.id(),
                deployment.id(), provider.id(), aiModel.id(),
                eventConfig.environment().name(), null, null, null, traceId,
                triggerSource, null);
        executionLogRepository.save(log);

        log.markRunning();
        executionLogRepository.save(log);

        String outputText = null;

        try {
            AiProviderAdapter adapter = adapterRegistry.getAdapter(provider.code().value());
            AiProviderRequest providerRequest = new AiProviderRequest(
                    provider.id(), deployment.providerDeploymentId(), renderedPrompt,
                    deployment.defaultTemperature(), deployment.defaultMaxOutputTokens());

            AiProviderResponse providerResponse = adapter.call(providerRequest);
            outputText = providerResponse.outputText();
            schemaValidator.validateOutput(eventDef.outputSchema(), outputText);

            log.markSucceeded(providerResponse.inputTokenCount(), providerResponse.outputTokenCount(),
                    providerResponse.totalTokenCount(), providerResponse.estimatedCost(),
                    providerResponse.providerRequestId(), null);
            executionLogRepository.save(log);

            activityLogger.logSuccess(AiAgentEntityTypes.EXECUTION_LOG, log.id(),
                    activityAction, "Execution succeeded for requestId: " + resolvedRequestId);

        } catch (Exception e) {
            String errorCode = e instanceof AppException ae ? ae.getErrorCode() : "PROVIDER_ERROR";
            String errorMessage = sanitizeError(e.getMessage());

            log.markFailed(errorCode, errorMessage, null, null, null, null, null, null);
            executionLogRepository.save(log);

            activityLogger.logSuccess(AiAgentEntityTypes.EXECUTION_LOG, log.id(),
                    AiAgentActivityActions.MARK_EXECUTION_FAILED,
                    "Execution failed for requestId: " + resolvedRequestId + " error: " + errorCode);

            if (e instanceof AppException) throw (AppException) e;
            throw AiAgentExceptions.openAiApiCallFailed(0);
        }

        return new ExecutionRunResponse(
                log.id(), log.requestId().value(), log.status().name(),
                log.eventConfigId(), log.eventDefinitionId(),
                log.agentId(), log.promptVersionId(), log.modelDeploymentId(),
                provider.code().value(), aiModel.code().value(), deployment.code().value(),
                outputText, log.inputTokenCount(), log.outputTokenCount(), log.totalTokenCount(),
                log.estimatedCost(), log.latencyMs(), log.providerRequestId(),
                log.errorCode(), log.errorMessage(),
                policyResult.decision().name(),
                policyResult.warnings().stream()
                        .map(w -> new UsagePolicyWarningInfo(
                                w.policyCode(), w.targetType(), w.period(),
                                w.metricName(), w.limitValue(), w.currentValue(), w.message()))
                        .toList());
    }

    private static String sanitizeError(String message) {
        if (message == null) return null;
        String lower = message.toLowerCase();
        if (lower.contains("api") && lower.contains("key")) return "Provider call failed";
        if (lower.contains("authorization") || lower.contains("bearer ")) return "Provider auth failed";
        if (message.length() <= 2000) return message;
        return message.substring(0, 2000) + "...(truncated)";
    }
}
