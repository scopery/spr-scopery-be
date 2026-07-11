package com.company.scopery.modules.aiagent.execution.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.integration.ai.AiProviderAdapter;
import com.company.scopery.integration.ai.AiProviderAdapterRegistry;
import com.company.scopery.integration.ai.AiProviderResponse;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentStatus;
import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.aimodel.domain.enums.AiModelStatus;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.aimodel.domain.valueobject.AiModelCode;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentEnvironment;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentStatus;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.valueobject.ModelDeploymentCode;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventConfigEnvironment;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventConfigStatus;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfig;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfigRepository;
import com.company.scopery.modules.aiagent.execution.application.command.ExecuteEventConfigCommand;
import com.company.scopery.modules.aiagent.execution.application.prompt.PromptRenderer;
import com.company.scopery.modules.aiagent.execution.application.response.ExecutionRunResponse;
import com.company.scopery.modules.aiagent.execution.application.service.AiExecutionSchemaValidator;
import com.company.scopery.modules.aiagent.execution.domain.model.ExecutionLog;
import com.company.scopery.modules.aiagent.execution.domain.model.ExecutionLogRepository;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptTemplateStatus;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptVersionStatus;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplate;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplateRepository;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersionRepository;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderStatus;
import com.company.scopery.modules.aiagent.provider.domain.model.Provider;
import com.company.scopery.modules.aiagent.provider.domain.model.ProviderRepository;
import com.company.scopery.modules.aiagent.provider.domain.valueobject.ProviderCode;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.usagepolicy.application.evaluator.UsagePolicyDecision;
import com.company.scopery.modules.aiagent.usagepolicy.application.evaluator.UsagePolicyEvaluationResult;
import com.company.scopery.modules.aiagent.usagepolicy.application.evaluator.UsagePolicyEvaluator;
import com.company.scopery.modules.aiagent.usagepolicy.application.evaluator.UsagePolicyViolation;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDefinitionStatus;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExecuteEventConfigActionTest {

    @Mock private EventDefinitionRepository eventDefinitionRepository;
    @Mock private EventConfigRepository eventConfigRepository;
    @Mock private AgentRepository agentRepository;
    @Mock private PromptVersionRepository promptVersionRepository;
    @Mock private PromptTemplateRepository promptTemplateRepository;
    @Mock private ModelDeploymentRepository modelDeploymentRepository;
    @Mock private AiModelRepository aiModelRepository;
    @Mock private ProviderRepository providerRepository;
    @Mock private ExecutionLogRepository executionLogRepository;
    @Mock private PromptRenderer promptRenderer;
    @Mock private AiProviderAdapterRegistry adapterRegistry;
    @Mock private AiAgentActivityLogger activityLogger;
    @Mock private UsagePolicyEvaluator usagePolicyEvaluator;
    @Mock private AiExecutionSchemaValidator schemaValidator;

    private ExecuteEventConfigAction action;

    private final UUID eventDefinitionId = UUID.randomUUID();
    private final UUID eventConfigId     = UUID.randomUUID();
    private final UUID agentId           = UUID.randomUUID();
    private final UUID promptVersionId   = UUID.randomUUID();
    private final UUID templateId        = UUID.randomUUID();
    private final UUID deploymentId      = UUID.randomUUID();
    private final UUID modelId           = UUID.randomUUID();
    private final UUID providerId        = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        action = new ExecuteEventConfigAction(
                eventConfigRepository, eventDefinitionRepository,
                agentRepository, promptVersionRepository, promptTemplateRepository,
                modelDeploymentRepository, aiModelRepository, providerRepository,
                executionLogRepository, promptRenderer, adapterRegistry, activityLogger,
                usagePolicyEvaluator, schemaValidator);

        when(usagePolicyEvaluator.evaluate(any()))
                .thenReturn(new UsagePolicyEvaluationResult(UsagePolicyDecision.ALLOWED, List.of(), List.of()));
    }

    @Test
    void execute_success_returnsSucceededResponse() {
        stubFullChainUpToProvider();

        AiProviderAdapter mockAdapter = mock(AiProviderAdapter.class);
        when(adapterRegistry.getAdapter("OPENAI")).thenReturn(mockAdapter);
        when(mockAdapter.call(any())).thenReturn(
                new AiProviderResponse("resp-123", "output text", 100, 200, 300, null));

        ExecuteEventConfigCommand command = new ExecuteEventConfigCommand(
                "req-001", eventConfigId, null, Map.of());
        ExecutionRunResponse response = action.execute(command);

        assertThat(response.requestId()).isEqualTo("req-001");
        assertThat(response.status()).isEqualTo("SUCCEEDED");
        assertThat(response.outputText()).isEqualTo("output text");
        assertThat(response.inputTokenCount()).isEqualTo(100);
        assertThat(response.outputTokenCount()).isEqualTo(200);
        assertThat(response.totalTokenCount()).isEqualTo(300);
        verify(executionLogRepository, times(3)).save(any());
    }

    @Test
    void execute_eventConfigNotFound_throwsNotFound() {
        when(eventConfigRepository.findById(eventConfigId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> action.execute(
                new ExecuteEventConfigCommand("req-001", eventConfigId, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ae.getErrorCode())
                            .isEqualTo(AiAgentErrorCatalog.EXECUTION_EVENT_CONFIG_NOT_FOUND.code());
                });
    }

    @Test
    void execute_eventConfigNotActive_throwsUnprocessable() {
        EventConfig inactiveConfig = mock(EventConfig.class);
        when(inactiveConfig.status()).thenReturn(EventConfigStatus.INACTIVE);
        when(eventConfigRepository.findById(eventConfigId)).thenReturn(Optional.of(inactiveConfig));
        when(inactiveConfig.id()).thenReturn(eventConfigId);

        assertThatThrownBy(() -> action.execute(
                new ExecuteEventConfigCommand("req-001", eventConfigId, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode())
                            .isEqualTo(AiAgentErrorCatalog.EXECUTION_EVENT_CONFIG_NOT_ACTIVE.code());
                });
    }

    @Test
    void execute_deploymentEnvironmentMismatch_throwsUnprocessable() {
        EventConfig eventConfig       = buildActiveEventConfig();
        EventDefinition eventDef      = buildActiveEventDefinition();
        Agent agent                   = buildActiveAgent();
        PromptVersion promptVersion   = buildActivePromptVersion();
        PromptTemplate promptTemplate = buildActivePromptTemplate();
        ModelDeployment prodDeployment = buildActiveDeployment(ModelDeploymentEnvironment.PROD);

        when(eventConfigRepository.findById(eventConfigId)).thenReturn(Optional.of(eventConfig));
        when(eventDefinitionRepository.findById(eventDefinitionId)).thenReturn(Optional.of(eventDef));
        when(executionLogRepository.existsByRequestId(any())).thenReturn(false);
        when(agentRepository.findById(agentId)).thenReturn(Optional.of(agent));
        when(promptVersionRepository.findById(promptVersionId)).thenReturn(Optional.of(promptVersion));
        when(promptTemplateRepository.findById(templateId)).thenReturn(Optional.of(promptTemplate));
        when(modelDeploymentRepository.findById(deploymentId)).thenReturn(Optional.of(prodDeployment));

        assertThatThrownBy(() -> action.execute(
                new ExecuteEventConfigCommand("req-001", eventConfigId, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode())
                            .isEqualTo(AiAgentErrorCatalog.EXECUTION_MODEL_DEPLOYMENT_ENVIRONMENT_MISMATCH.code());
                });
    }

    @Test
    void execute_providerAdapterFails_marksFailedAndRethrows() {
        stubFullChainUpToProvider();

        AiProviderAdapter mockAdapter = mock(AiProviderAdapter.class);
        when(adapterRegistry.getAdapter("OPENAI")).thenReturn(mockAdapter);
        when(mockAdapter.call(any())).thenThrow(AiAgentExceptions.openAiApiTimeout());

        assertThatThrownBy(() -> action.execute(
                new ExecuteEventConfigCommand("req-001", eventConfigId, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
                });

        verify(executionLogRepository, times(3)).save(any(ExecutionLog.class));
    }

    @Test
    void execute_blockPolicy_throwsUsagePolicyExceeded() {
        stubFullChainUpToProvider();

        UsagePolicyViolation violation = new UsagePolicyViolation(
                UUID.randomUUID(), "GLOBAL_REQUEST_LIMIT", "GLOBAL", "DAY",
                "REQUEST_COUNT", "100", "100", "BLOCK",
                "Request count 100 reached limit 100 for policy GLOBAL_REQUEST_LIMIT");
        when(usagePolicyEvaluator.evaluate(any()))
                .thenReturn(new UsagePolicyEvaluationResult(UsagePolicyDecision.BLOCKED, List.of(violation), List.of()));

        assertThatThrownBy(() -> action.execute(
                new ExecuteEventConfigCommand("req-block", eventConfigId, null, Map.of())))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
                    assertThat(ae.getErrorCode())
                            .isEqualTo(AiAgentErrorCatalog.USAGE_POLICY_EXCEEDED.code());
                });

        verify(executionLogRepository, never()).save(any());
    }

    @Test
    void execute_warnPolicy_allowsCallAndIncludesWarning() {
        stubFullChainUpToProvider();

        AiProviderAdapter mockAdapter = mock(AiProviderAdapter.class);
        when(adapterRegistry.getAdapter("OPENAI")).thenReturn(mockAdapter);
        when(mockAdapter.call(any())).thenReturn(
                new AiProviderResponse("resp-w", "output", 50, 100, 150, null));

        UsagePolicyViolation warning = new UsagePolicyViolation(
                UUID.randomUUID(), "AGENT_COST_WARN", "AGENT", "MONTH",
                "ESTIMATED_COST", "10.00", "9.50", "WARN",
                "Estimated cost 9.50 reached limit 10.00 for policy AGENT_COST_WARN");
        when(usagePolicyEvaluator.evaluate(any()))
                .thenReturn(new UsagePolicyEvaluationResult(UsagePolicyDecision.WARN, List.of(), List.of(warning)));

        ExecutionRunResponse response = action.execute(
                new ExecuteEventConfigCommand("req-warn", eventConfigId, null, Map.of()));

        assertThat(response.usagePolicyDecision()).isEqualTo("WARN");
        assertThat(response.usagePolicyWarnings()).hasSize(1);
        assertThat(response.usagePolicyWarnings().get(0).policyCode()).isEqualTo("AGENT_COST_WARN");
        assertThat(response.status()).isEqualTo("SUCCEEDED");
    }

    @Test
    void execute_allowedPolicy_includesPolicyDecisionInResponse() {
        stubFullChainUpToProvider();

        AiProviderAdapter mockAdapter = mock(AiProviderAdapter.class);
        when(adapterRegistry.getAdapter("OPENAI")).thenReturn(mockAdapter);
        when(mockAdapter.call(any())).thenReturn(
                new AiProviderResponse("resp-ok", "output", 10, 20, 30, null));

        ExecutionRunResponse response = action.execute(
                new ExecuteEventConfigCommand("req-allowed", eventConfigId, null, Map.of()));

        assertThat(response.usagePolicyDecision()).isEqualTo("ALLOWED");
        assertThat(response.usagePolicyWarnings()).isEmpty();
    }

    private void stubFullChainUpToProvider() {
        EventConfig eventConfig       = buildActiveEventConfig();
        EventDefinition eventDef      = buildActiveEventDefinition();
        Agent agent                   = buildActiveAgent();
        PromptVersion promptVersion   = buildActivePromptVersion();
        PromptTemplate promptTemplate = buildActivePromptTemplate();
        ModelDeployment deployment    = buildActiveDeployment(ModelDeploymentEnvironment.DEV);
        AiModel aiModel               = buildActiveAiModel();
        Provider provider             = buildActiveProvider("OPENAI");

        when(eventConfigRepository.findById(eventConfigId)).thenReturn(Optional.of(eventConfig));
        when(eventDefinitionRepository.findById(eventDefinitionId)).thenReturn(Optional.of(eventDef));
        when(agentRepository.findById(agentId)).thenReturn(Optional.of(agent));
        when(promptVersionRepository.findById(promptVersionId)).thenReturn(Optional.of(promptVersion));
        when(promptTemplateRepository.findById(templateId)).thenReturn(Optional.of(promptTemplate));
        when(modelDeploymentRepository.findById(deploymentId)).thenReturn(Optional.of(deployment));
        when(aiModelRepository.findById(modelId)).thenReturn(Optional.of(aiModel));
        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));
        when(promptRenderer.render(any(), any())).thenReturn("Rendered prompt");
        when(executionLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(executionLogRepository.existsByRequestId(any())).thenReturn(false);
    }

    private EventConfig buildActiveEventConfig() {
        EventConfig ec = mock(EventConfig.class);
        when(ec.id()).thenReturn(eventConfigId);
        when(ec.status()).thenReturn(EventConfigStatus.ACTIVE);
        when(ec.agentId()).thenReturn(agentId);
        when(ec.promptVersionId()).thenReturn(promptVersionId);
        when(ec.modelDeploymentId()).thenReturn(deploymentId);
        when(ec.eventDefinitionId()).thenReturn(eventDefinitionId);
        when(ec.environment()).thenReturn(EventConfigEnvironment.DEV);
        return ec;
    }

    private EventDefinition buildActiveEventDefinition() {
        EventDefinition ed = mock(EventDefinition.class);
        when(ed.id()).thenReturn(eventDefinitionId);
        when(ed.status()).thenReturn(EventDefinitionStatus.ACTIVE);
        return ed;
    }

    private Agent buildActiveAgent() {
        Agent agent = mock(Agent.class);
        when(agent.id()).thenReturn(agentId);
        when(agent.status()).thenReturn(AgentStatus.ACTIVE);
        return agent;
    }

    private PromptVersion buildActivePromptVersion() {
        PromptVersion pv = mock(PromptVersion.class);
        when(pv.id()).thenReturn(promptVersionId);
        when(pv.status()).thenReturn(PromptVersionStatus.ACTIVE);
        when(pv.templateId()).thenReturn(templateId);
        when(pv.content()).thenReturn("Hello, {{name}}!");
        return pv;
    }

    private PromptTemplate buildActivePromptTemplate() {
        PromptTemplate pt = mock(PromptTemplate.class);
        when(pt.id()).thenReturn(templateId);
        when(pt.status()).thenReturn(PromptTemplateStatus.ACTIVE);
        when(pt.agentId()).thenReturn(agentId);
        return pt;
    }

    private ModelDeployment buildActiveDeployment(ModelDeploymentEnvironment env) {
        ModelDeployment dep = mock(ModelDeployment.class);
        when(dep.id()).thenReturn(deploymentId);
        when(dep.status()).thenReturn(ModelDeploymentStatus.ACTIVE);
        when(dep.environment()).thenReturn(env);
        when(dep.modelId()).thenReturn(modelId);
        when(dep.providerDeploymentId()).thenReturn("gpt-4o");
        when(dep.defaultTemperature()).thenReturn(new BigDecimal("1.0"));
        when(dep.defaultMaxOutputTokens()).thenReturn(4096);
        ModelDeploymentCode depCode = mock(ModelDeploymentCode.class);
        when(depCode.value()).thenReturn("gpt-4o-dep");
        when(dep.code()).thenReturn(depCode);
        return dep;
    }

    private AiModel buildActiveAiModel() {
        AiModel m = mock(AiModel.class);
        when(m.id()).thenReturn(modelId);
        when(m.status()).thenReturn(AiModelStatus.ACTIVE);
        when(m.providerId()).thenReturn(providerId);
        AiModelCode modelCode = mock(AiModelCode.class);
        when(modelCode.value()).thenReturn("GPT-4O");
        when(m.code()).thenReturn(modelCode);
        return m;
    }

    private Provider buildActiveProvider(String code) {
        Provider p = mock(Provider.class);
        when(p.id()).thenReturn(providerId);
        when(p.status()).thenReturn(ProviderStatus.ACTIVE);
        ProviderCode providerCode = mock(ProviderCode.class);
        when(providerCode.value()).thenReturn(code);
        when(p.code()).thenReturn(providerCode);
        return p;
    }
}
