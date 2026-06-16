package com.company.scopery.modules.aiagent.execution.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.integration.ai.AiProviderAdapter;
import com.company.scopery.integration.ai.AiProviderAdapterRegistry;
import com.company.scopery.integration.ai.AiProviderRequest;
import com.company.scopery.integration.ai.AiProviderResponse;
import com.company.scopery.modules.aiagent.agent.domain.Agent;
import com.company.scopery.modules.aiagent.agent.domain.AgentRepository;
import com.company.scopery.modules.aiagent.agent.domain.AgentStatus;
import com.company.scopery.modules.aiagent.aimodel.domain.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.AiModelRepository;
import com.company.scopery.modules.aiagent.aimodel.domain.AiModelStatus;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentStatus;
import com.company.scopery.modules.aiagent.eventconfig.domain.EventConfig;
import com.company.scopery.modules.aiagent.eventconfig.domain.EventConfigEnvironment;
import com.company.scopery.modules.aiagent.eventconfig.domain.EventConfigRepository;
import com.company.scopery.modules.aiagent.eventconfig.domain.EventConfigStatus;
import com.company.scopery.modules.aiagent.execution.application.command.ExecuteEventCommand;
import com.company.scopery.modules.aiagent.execution.application.command.ExecuteEventConfigCommand;
import com.company.scopery.modules.aiagent.execution.application.command.ExecutePlaygroundDirectCommand;
import com.company.scopery.modules.aiagent.execution.application.prompt.PromptRenderer;
import com.company.scopery.modules.aiagent.execution.application.response.ExecutionRunResponse;
import com.company.scopery.modules.aiagent.execution.domain.ExecutionLog;
import com.company.scopery.modules.aiagent.execution.domain.ExecutionLogRepository;
import com.company.scopery.modules.aiagent.execution.domain.ExecutionRequestId;
import com.company.scopery.modules.aiagent.execution.domain.ExecutionTriggerSource;
import com.company.scopery.modules.aiagent.prompt.domain.PromptTemplate;
import com.company.scopery.modules.aiagent.prompt.domain.PromptTemplateRepository;
import com.company.scopery.modules.aiagent.prompt.domain.PromptTemplateStatus;
import com.company.scopery.modules.aiagent.prompt.domain.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.domain.PromptVersionRepository;
import com.company.scopery.modules.aiagent.prompt.domain.PromptVersionStatus;
import com.company.scopery.modules.aiagent.provider.domain.Provider;
import com.company.scopery.modules.aiagent.provider.domain.ProviderRepository;
import com.company.scopery.modules.aiagent.provider.domain.ProviderStatus;
import com.company.scopery.modules.aiagent.execution.application.response.UsagePolicyWarningInfo;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import com.company.scopery.modules.aiagent.usagepolicy.application.evaluator.UsagePolicyEvaluationContext;
import com.company.scopery.modules.aiagent.usagepolicy.application.evaluator.UsagePolicyEvaluationResult;
import com.company.scopery.modules.aiagent.usagepolicy.application.evaluator.UsagePolicyEvaluator;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinitionStatus;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventKey;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.SourceSystemCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class ExecutionApplicationService {

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
    private final String runtimeEnvironment;

    public ExecutionApplicationService(EventDefinitionRepository eventDefinitionRepository,
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
        this.runtimeEnvironment = runtimeEnvironment;
    }

    public ExecutionRunResponse executeEvent(ExecuteEventCommand command) {
        // Resolve environment — use runtimeEnvironment when caller omits it
        String envStr = (command.environment() != null && !command.environment().isBlank())
                ? command.environment() : runtimeEnvironment;
        EventConfigEnvironment environment = AiAgentEnumParser.parseRequired(
                EventConfigEnvironment.class, envStr,
                AiAgentErrorCatalog.INVALID_EVENT_CONFIG_ENVIRONMENT.code(), "environment");

        // Resolve triggerSource — default is API
        ExecutionTriggerSource triggerSource = resolveTriggerSource(
                command.triggerSource(), ExecutionTriggerSource.API);

        // Resolve EventDefinition via one of three paths
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
            // Backward-compatible path: find by EventDefinition code
            if (command.eventCode() == null || command.eventCode().isBlank()) {
                throw AiAgentExceptions.executionEventDefinitionNotFound(
                        "eventDefinitionId, sourceSystem+eventKey, or eventCode is required");
            }
            com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinitionCode code =
                    com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinitionCode.of(command.eventCode());
            eventDef = eventDefinitionRepository.findByCode(code)
                    .orElseThrow(() -> AiAgentExceptions.executionEventDefinitionNotFound(command.eventCode()));
        }

        if (eventDef.status() != EventDefinitionStatus.ACTIVE) {
            throw AiAgentExceptions.executionEventDefinitionNotActive(eventDef.id());
        }

        // Resolve active EventConfig for this event definition and environment
        EventConfig eventConfig = eventConfigRepository
                .findActiveByEventDefinitionIdAndEnvironment(eventDef.id(), environment)
                .orElseThrow(() -> AiAgentExceptions.executionEventConfigNotFound(
                        eventDef.id(), environment.name()));

        return orchestrate(eventConfig, command.requestId(), command.inputVariables(),
                triggerSource, AiAgentActivityActions.EXECUTE_EVENT);
    }

    public ExecutionRunResponse executeEventConfig(ExecuteEventConfigCommand command) {
        EventConfig eventConfig = eventConfigRepository.findById(command.eventConfigId())
                .orElseThrow(() -> AiAgentExceptions.executionEventConfigNotFoundById(command.eventConfigId()));

        if (eventConfig.status() != EventConfigStatus.ACTIVE) {
            throw AiAgentExceptions.executionEventConfigNotActive(eventConfig.id());
        }

        // Resolve triggerSource — default is MANUAL
        ExecutionTriggerSource triggerSource = resolveTriggerSource(
                command.triggerSource(), ExecutionTriggerSource.MANUAL);

        return orchestrate(eventConfig, command.requestId(), command.inputVariables(),
                triggerSource, AiAgentActivityActions.EXECUTE_EVENT_CONFIG);
    }

    public ExecutionRunResponse executePlaygroundDirect(ExecutePlaygroundDirectCommand command) {
        String resolvedRequestId = (command.requestId() != null && !command.requestId().isBlank())
                ? command.requestId().trim() : UUID.randomUUID().toString();

        ExecutionRequestId execRequestId = ExecutionRequestId.of(resolvedRequestId);
        if (executionLogRepository.existsByRequestId(execRequestId)) {
            throw AiAgentExceptions.executionLogRequestIdAlreadyExists(resolvedRequestId);
        }

        Agent agent = agentRepository.findById(command.agentId())
                .orElseThrow(() -> AiAgentExceptions.playgroundAgentNotFound(command.agentId()));
        if (agent.status() != AgentStatus.ACTIVE) {
            throw AiAgentExceptions.playgroundAgentNotActive(agent.id());
        }

        PromptVersion promptVersion = promptVersionRepository.findById(command.promptVersionId())
                .orElseThrow(() -> AiAgentExceptions.playgroundPromptVersionNotFound(command.promptVersionId()));
        if (promptVersion.status() != PromptVersionStatus.ACTIVE) {
            throw AiAgentExceptions.playgroundPromptVersionNotActive(promptVersion.id());
        }

        PromptTemplate promptTemplate = promptTemplateRepository.findById(promptVersion.templateId())
                .orElseThrow(() -> AiAgentExceptions.playgroundPromptTemplateNotFound(promptVersion.templateId()));
        if (promptTemplate.status() != PromptTemplateStatus.ACTIVE) {
            throw AiAgentExceptions.playgroundPromptTemplateNotActive(promptTemplate.id());
        }
        if (!promptTemplate.agentId().equals(command.agentId())) {
            throw AiAgentExceptions.playgroundPromptTemplateAgentMismatch(promptTemplate.id(), command.agentId());
        }

        ModelDeployment deployment = modelDeploymentRepository.findById(command.modelDeploymentId())
                .orElseThrow(() -> AiAgentExceptions.playgroundModelDeploymentNotFound(command.modelDeploymentId()));
        if (deployment.status() != ModelDeploymentStatus.ACTIVE) {
            throw AiAgentExceptions.playgroundModelDeploymentNotActive(deployment.id());
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

        UsagePolicyEvaluationContext policyContext = new UsagePolicyEvaluationContext(
                null, null, agent.id(), deployment.id(),
                promptVersion.id(), provider.id(), resolvedRequestId,
                ExecutionTriggerSource.PLAYGROUND.name(), Instant.now());
        UsagePolicyEvaluationResult policyResult = usagePolicyEvaluator.evaluate(policyContext);
        if (policyResult.isBlocked()) {
            throw AiAgentExceptions.usagePolicyExceeded(
                    policyResult.violations().isEmpty() ? "" : policyResult.violations().get(0).policyCode());
        }

        String renderedPrompt = promptRenderer.render(promptVersion.content(), command.inputVariables());

        ExecutionLog log = ExecutionLog.create(
                execRequestId, null, null,
                agent.id(), promptVersion.id(), deployment.id(),
                ExecutionTriggerSource.PLAYGROUND, null);
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

            log.markSucceeded(providerResponse.inputTokenCount(), providerResponse.outputTokenCount(),
                    providerResponse.totalTokenCount(), providerResponse.estimatedCost(),
                    providerResponse.providerRequestId(), null);
            executionLogRepository.save(log);

            activityLogger.logSuccess(AiAgentEntityTypes.EXECUTION_LOG, log.id(),
                    AiAgentActivityActions.EXECUTE_EVENT_CONFIG,
                    "Playground direct execution succeeded for requestId: " + resolvedRequestId);

        } catch (Exception e) {
            String errorCode = e instanceof AppException ae ? ae.getErrorCode() : "PROVIDER_ERROR";
            String errorMessage = e.getMessage();

            log.markFailed(errorCode, errorMessage, null, null, null, null, null, null);
            executionLogRepository.save(log);

            activityLogger.logSuccess(AiAgentEntityTypes.EXECUTION_LOG, log.id(),
                    AiAgentActivityActions.MARK_EXECUTION_FAILED,
                    "Playground direct execution failed for requestId: " + resolvedRequestId + " error: " + errorCode);

            if (e instanceof AppException) throw (AppException) e;
            throw AiAgentExceptions.openAiApiCallFailed(0, errorMessage);
        }

        return new ExecutionRunResponse(
                log.id(), log.requestId().value(), log.status().name(),
                null, null,
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

    private ExecutionTriggerSource resolveTriggerSource(String raw, ExecutionTriggerSource defaultSource) {
        ExecutionTriggerSource parsed = AiAgentEnumParser.parseOptional(
                ExecutionTriggerSource.class, raw,
                AiAgentErrorCatalog.INVALID_EXECUTION_TRIGGER_SOURCE.code(), "triggerSource");
        return parsed != null ? parsed : defaultSource;
    }

    // No @Transactional — each repository.save() commits in its own transaction,
    // ensuring FAILED status is persisted even when the provider call throws.
    private ExecutionRunResponse orchestrate(EventConfig eventConfig, String requestId,
                                              Map<String, String> inputVariables,
                                              ExecutionTriggerSource triggerSource,
                                              String activityAction) {

        // Use provided requestId (must not be blank per Bean Validation); generate if somehow empty
        String resolvedRequestId = (requestId != null && !requestId.isBlank())
                ? requestId.trim() : UUID.randomUUID().toString();

        // Validate requestId uniqueness
        ExecutionRequestId execRequestId = ExecutionRequestId.of(resolvedRequestId);
        if (executionLogRepository.existsByRequestId(execRequestId)) {
            throw AiAgentExceptions.executionLogRequestIdAlreadyExists(resolvedRequestId);
        }

        // Load and validate EventDefinition
        EventDefinition eventDef = eventDefinitionRepository.findById(eventConfig.eventDefinitionId())
                .orElseThrow(() -> AiAgentExceptions.executionEventDefinitionNotFound(
                        eventConfig.eventDefinitionId().toString()));
        if (eventDef.status() != EventDefinitionStatus.ACTIVE) {
            throw AiAgentExceptions.executionEventDefinitionNotActive(eventDef.id());
        }

        // Load and validate Agent
        Agent agent = agentRepository.findById(eventConfig.agentId())
                .orElseThrow(() -> AiAgentExceptions.executionAgentNotFound(eventConfig.agentId()));
        if (agent.status() != AgentStatus.ACTIVE) {
            throw AiAgentExceptions.executionAgentNotActive(agent.id());
        }

        // Load and validate PromptVersion
        PromptVersion promptVersion = promptVersionRepository.findById(eventConfig.promptVersionId())
                .orElseThrow(() -> AiAgentExceptions.executionPromptVersionNotFound(eventConfig.promptVersionId()));
        if (promptVersion.status() != PromptVersionStatus.ACTIVE) {
            throw AiAgentExceptions.executionPromptVersionNotActive(promptVersion.id());
        }

        // Load and validate PromptTemplate
        PromptTemplate promptTemplate = promptTemplateRepository.findById(promptVersion.templateId())
                .orElseThrow(() -> AiAgentExceptions.executionPromptTemplateNotFound(promptVersion.templateId()));
        if (promptTemplate.status() != PromptTemplateStatus.ACTIVE) {
            throw AiAgentExceptions.executionPromptTemplateNotActive(promptTemplate.id());
        }

        // Validate PromptTemplate belongs to the same Agent as EventConfig
        if (!promptTemplate.agentId().equals(eventConfig.agentId())) {
            throw AiAgentExceptions.executionPromptTemplateAgentMismatch(
                    promptTemplate.id(), eventConfig.agentId());
        }

        // Load and validate ModelDeployment
        ModelDeployment deployment = modelDeploymentRepository.findById(eventConfig.modelDeploymentId())
                .orElseThrow(() -> AiAgentExceptions.executionModelDeploymentNotFound(eventConfig.modelDeploymentId()));
        if (deployment.status() != ModelDeploymentStatus.ACTIVE) {
            throw AiAgentExceptions.executionModelDeploymentNotActive(deployment.id());
        }

        // Validate ModelDeployment environment matches EventConfig environment
        if (!deployment.environment().name().equals(eventConfig.environment().name())) {
            throw AiAgentExceptions.executionModelDeploymentEnvironmentMismatch(
                    deployment.environment().name(), eventConfig.environment().name());
        }

        // Load and validate AiModel
        AiModel aiModel = aiModelRepository.findById(deployment.modelId())
                .orElseThrow(() -> AiAgentExceptions.executionAiModelNotFound(deployment.modelId()));
        if (aiModel.status() != AiModelStatus.ACTIVE) {
            throw AiAgentExceptions.executionAiModelNotActive(aiModel.id());
        }

        // Load and validate Provider
        Provider provider = providerRepository.findById(aiModel.providerId())
                .orElseThrow(() -> AiAgentExceptions.executionProviderNotFound(aiModel.providerId()));
        if (provider.status() != ProviderStatus.ACTIVE) {
            throw AiAgentExceptions.executionProviderNotActive(provider.id());
        }

        // Evaluate usage policies before creating execution log — BLOCKED aborts without writing any log
        UsagePolicyEvaluationContext policyContext = new UsagePolicyEvaluationContext(
                eventConfig.id(), eventConfig.eventDefinitionId(), agent.id(), deployment.id(),
                promptVersion.id(), provider.id(), resolvedRequestId, triggerSource.name(), Instant.now());
        UsagePolicyEvaluationResult policyResult = usagePolicyEvaluator.evaluate(policyContext);
        if (policyResult.isBlocked()) {
            throw AiAgentExceptions.usagePolicyExceeded(
                    policyResult.violations().isEmpty() ? "" : policyResult.violations().get(0).policyCode());
        }

        // Render prompt — inputVariables must not be stored or logged
        String renderedPrompt = promptRenderer.render(promptVersion.content(), inputVariables);

        // Create ExecutionLog with PENDING status and persist
        ExecutionLog log = ExecutionLog.create(
                execRequestId,
                eventConfig.id(),
                eventConfig.eventDefinitionId(),
                agent.id(),
                promptVersion.id(),
                deployment.id(),
                triggerSource,
                null);
        executionLogRepository.save(log);

        // Transition to RUNNING
        log.markRunning();
        executionLogRepository.save(log);

        String outputText = null;

        try {
            AiProviderAdapter adapter = adapterRegistry.getAdapter(provider.code().value());
            AiProviderRequest providerRequest = new AiProviderRequest(
                    provider.id(),
                    deployment.providerDeploymentId(),
                    renderedPrompt,
                    deployment.defaultTemperature(),
                    deployment.defaultMaxOutputTokens());

            AiProviderResponse providerResponse = adapter.call(providerRequest);

            // Capture outputText for API response only — not stored in ExecutionLog
            outputText = providerResponse.outputText();

            log.markSucceeded(
                    providerResponse.inputTokenCount(),
                    providerResponse.outputTokenCount(),
                    providerResponse.totalTokenCount(),
                    providerResponse.estimatedCost(),
                    providerResponse.providerRequestId(),
                    null);
            executionLogRepository.save(log);

            activityLogger.logSuccess(AiAgentEntityTypes.EXECUTION_LOG, log.id(),
                    activityAction, "Execution succeeded for requestId: " + resolvedRequestId);

        } catch (Exception e) {
            String errorCode = e instanceof AppException ae ? ae.getErrorCode() : "PROVIDER_ERROR";
            String errorMessage = e.getMessage();

            log.markFailed(errorCode, errorMessage, null, null, null, null, null, null);
            executionLogRepository.save(log);

            activityLogger.logSuccess(AiAgentEntityTypes.EXECUTION_LOG, log.id(),
                    AiAgentActivityActions.MARK_EXECUTION_FAILED,
                    "Execution failed for requestId: " + resolvedRequestId + " error: " + errorCode);

            if (e instanceof AppException) throw (AppException) e;
            throw AiAgentExceptions.openAiApiCallFailed(0, errorMessage);
        }

        return new ExecutionRunResponse(
                log.id(),
                log.requestId().value(),
                log.status().name(),
                log.eventConfigId(),
                log.eventDefinitionId(),
                log.agentId(),
                log.promptVersionId(),
                log.modelDeploymentId(),
                provider.code().value(),
                aiModel.code().value(),
                deployment.code().value(),
                outputText,
                log.inputTokenCount(),
                log.outputTokenCount(),
                log.totalTokenCount(),
                log.estimatedCost(),
                log.latencyMs(),
                log.providerRequestId(),
                log.errorCode(),
                log.errorMessage(),
                policyResult.decision().name(),
                policyResult.warnings().stream()
                        .map(w -> new UsagePolicyWarningInfo(
                                w.policyCode(), w.targetType(), w.period(),
                                w.metricName(), w.limitValue(), w.currentValue(), w.message()))
                        .toList());
    }
}
