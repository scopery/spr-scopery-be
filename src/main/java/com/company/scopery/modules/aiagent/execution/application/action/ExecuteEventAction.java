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
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventConfigEnvironment;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfig;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfigRepository;
import com.company.scopery.modules.aiagent.execution.application.command.ExecuteEventCommand;
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
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventKey;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.SourceSystemCode;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class ExecuteEventAction {

    private static final String EVENT_USAGE_POLICY_BLOCKED = "AI_USAGE_POLICY_BLOCKED";

    private final EventDefinitionRepository eventDefinitionRepository;
    private final EventConfigRepository eventConfigRepository;
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
    private final String runtimeEnvironment;

    public ExecuteEventAction(EventDefinitionRepository eventDefinitionRepository,
                               EventConfigRepository eventConfigRepository,
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
                               TransactionalOutboxService outboxService,
                               @Value("${scopery.aiagent.runtime-environment}") String runtimeEnvironment) {
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.eventConfigRepository = eventConfigRepository;
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
        this.runtimeEnvironment = runtimeEnvironment;
    }

    public ExecutionRunResponse execute(ExecuteEventCommand command) {
        String envStr = (command.environment() != null && !command.environment().isBlank())
                ? command.environment() : runtimeEnvironment;
        EventConfigEnvironment environment = AiAgentEnumParser.parseRequired(
                EventConfigEnvironment.class, envStr,
                AiAgentErrorCatalog.INVALID_EVENT_CONFIG_ENVIRONMENT.code(), "environment");

        ExecutionTriggerSource triggerSource = resolveTriggerSource(
                command.triggerSource(), ExecutionTriggerSource.API);

        EventDefinition eventDef;
        if (command.eventDefinitionId() != null) {
            eventDef = eventDefinitionRepository.findById(command.eventDefinitionId())
                    .orElseThrow(() -> AiAgentExceptions.executionEventDefinitionNotFound(
                            command.eventDefinitionId().toString()));
        } else if (command.sourceSystem() != null && !command.sourceSystem().isBlank()
                && command.eventKey() != null && !command.eventKey().isBlank()) {
            SourceSystemCode sourceSystem = SourceSystemCode.of(command.sourceSystem());
            EventKey eventKey = EventKey.of(command.eventKey());
            eventDef = eventDefinitionRepository.findBySourceSystemAndEventKey(sourceSystem, eventKey)
                    .orElseThrow(() -> AiAgentExceptions.executionEventDefinitionNotFound(
                            command.sourceSystem() + "+" + command.eventKey()));
        } else {
            if (command.eventCode() == null || command.eventCode().isBlank()) {
                throw AiAgentExceptions.executionEventDefinitionNotFound(
                        "eventDefinitionId, sourceSystem+eventKey, or eventCode is required");
            }
            com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode code =
                    com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode.of(command.eventCode());
            eventDef = eventDefinitionRepository.findByCode(code)
                    .orElseThrow(() -> AiAgentExceptions.executionEventDefinitionNotFound(command.eventCode()));
        }

        if (eventDef.status() != EventDefinitionStatus.ACTIVE) {
            throw AiAgentExceptions.executionEventDefinitionNotActive(eventDef.id());
        }

        EventConfig eventConfig = eventConfigRepository
                .findActiveByEventDefinitionIdAndEnvironment(eventDef.id(), environment)
                .orElseThrow(() -> AiAgentExceptions.executionEventConfigNotFound(
                        eventDef.id(), environment.name()));

        return orchestrate(eventConfig, command.requestId(), command.inputVariables(),
                triggerSource, AiAgentActivityActions.EXECUTE_EVENT);
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
                    AiAgentActivityActions.EXECUTE_EVENT,
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
