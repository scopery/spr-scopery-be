package com.company.scopery.modules.aiagent.execution.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.integration.ai.AiProviderAdapter;
import com.company.scopery.integration.ai.AiProviderAdapterRegistry;
import com.company.scopery.integration.ai.AiProviderRequest;
import com.company.scopery.integration.ai.AiProviderResponse;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentStatus;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.aimodel.domain.enums.AiModelStatus;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentStatus;
import com.company.scopery.modules.aiagent.execution.application.command.ExecutePlaygroundDirectCommand;
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
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.usagepolicy.application.evaluator.UsagePolicyEvaluationContext;
import com.company.scopery.modules.aiagent.usagepolicy.application.evaluator.UsagePolicyEvaluationResult;
import com.company.scopery.modules.aiagent.usagepolicy.application.evaluator.UsagePolicyEvaluator;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ExecutePlaygroundDirectAction {

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

    public ExecutePlaygroundDirectAction(AgentRepository agentRepository,
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
                                          AiExecutionSchemaValidator schemaValidator) {
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
    }

    // CLAUDE.md §21 normally requires @Transactional on every Action.execute(). This is a
    // documented exception: the AI provider call inside this method can take seconds and must
    // not hold a DB transaction open, and execution status (RUNNING -> SUCCEEDED/FAILED) must be
    // persisted in its own committed transaction even when the provider call throws midway, so a
    // failed run is still recorded instead of being rolled back with the rest of the method.
    public ExecutionRunResponse execute(ExecutePlaygroundDirectCommand command) {
        String resolvedRequestId = (command.requestId() != null && !command.requestId().isBlank())
                ? command.requestId().trim() : UUID.randomUUID().toString();

        ExecutionRequestId execRequestId = ExecutionRequestId.of(resolvedRequestId);
        if (executionLogRepository.existsByRequestId(execRequestId)) {
            throw AiAgentExceptions.executionLogRequestIdAlreadyExists(resolvedRequestId);
        }

        var agent = agentRepository.findById(command.agentId())
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

        schemaValidator.validateInput(null, promptVersion.variableSchema(),
                promptVersion.content(), command.inputVariables());

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
            String errorMessage = truncate(e.getMessage());

            log.markFailed(errorCode, errorMessage, null, null, null, null, null, null);
            executionLogRepository.save(log);

            activityLogger.logSuccess(AiAgentEntityTypes.EXECUTION_LOG, log.id(),
                    AiAgentActivityActions.MARK_EXECUTION_FAILED,
                    "Playground direct execution failed for requestId: " + resolvedRequestId + " error: " + errorCode);

            if (e instanceof AppException) throw (AppException) e;
            throw AiAgentExceptions.openAiApiCallFailed(0);
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

    // Caps what an arbitrary runtime exception can push into a TEXT column, in case a future
    // provider adapter's exception message ever embeds oversized or sensitive request/response detail.
    private static String truncate(String message) {
        if (message == null || message.length() <= 2000) return message;
        return message.substring(0, 2000) + "...(truncated)";
    }
}
