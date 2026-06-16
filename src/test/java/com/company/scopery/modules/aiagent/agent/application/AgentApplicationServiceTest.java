package com.company.scopery.modules.aiagent.agent.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.aiagent.agent.application.command.CreateAgentCommand;
import com.company.scopery.modules.aiagent.agent.application.response.AgentResponse;
import com.company.scopery.modules.aiagent.agent.domain.*;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentCode;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentEnvironment;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentStatus;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentApplicationServiceTest {

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private ModelDeploymentRepository deploymentRepository;

    @Mock
    private AiAgentActivityLogger activityLogger;

    private AgentApplicationService service;

    @BeforeEach
    void setUp() {
        service = new AgentApplicationService(agentRepository, deploymentRepository, activityLogger);
    }

    @Test
    void createAgent_withNullDeployment_succeeds() {
        CreateAgentCommand command = new CreateAgentCommand(
                "CV Extraction Agent", "CV_EXTRACTION_AGENT", "EXTRACTION",
                null, null, "JSON");

        when(agentRepository.existsByCode(AgentCode.of("CV_EXTRACTION_AGENT"))).thenReturn(false);
        when(agentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AgentResponse response = service.createAgent(command);

        assertThat(response.code()).isEqualTo("CV_EXTRACTION_AGENT");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.outputFormat()).isEqualTo("JSON");
        verify(agentRepository).save(any());
        verify(activityLogger).logSuccess(eq("AGENT"), any(UUID.class),
                eq("CREATE_AGENT"), any(String.class));
    }

    @Test
    void createAgent_normalizesCodeToUppercase() {
        CreateAgentCommand command = new CreateAgentCommand(
                "CV Extraction Agent", "cv_extraction_agent", "EXTRACTION",
                null, null, null);

        when(agentRepository.existsByCode(AgentCode.of("CV_EXTRACTION_AGENT"))).thenReturn(false);
        when(agentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AgentResponse response = service.createAgent(command);

        assertThat(response.code()).isEqualTo("CV_EXTRACTION_AGENT");
    }

    @Test
    void createAgent_duplicateCode_throwsAppExceptionWithConflict() {
        CreateAgentCommand command = new CreateAgentCommand(
                "CV Extraction Agent", "CV_EXTRACTION_AGENT", "EXTRACTION",
                null, null, null);

        when(agentRepository.existsByCode(AgentCode.of("CV_EXTRACTION_AGENT"))).thenReturn(true);

        assertThatThrownBy(() -> service.createAgent(command))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("CV_EXTRACTION_AGENT")
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(AiAgentErrorCatalog.AGENT_CODE_ALREADY_EXISTS.code());
                });

        verify(agentRepository, never()).save(any());
    }

    @Test
    void createAgent_withActiveDeployment_succeeds() {
        UUID deploymentId = UUID.randomUUID();
        CreateAgentCommand command = new CreateAgentCommand(
                "CV Extraction Agent", "CV_EXTRACTION_AGENT", "EXTRACTION",
                null, deploymentId, null);

        when(agentRepository.existsByCode(any())).thenReturn(false);
        when(deploymentRepository.findById(deploymentId)).thenReturn(Optional.of(activeDeployment(deploymentId)));
        when(agentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AgentResponse response = service.createAgent(command);

        assertThat(response.defaultModelDeploymentId()).isEqualTo(deploymentId);
    }

    @Test
    void createAgent_withInactiveDeployment_throwsAppExceptionWithUnprocessable() {
        UUID deploymentId = UUID.randomUUID();
        CreateAgentCommand command = new CreateAgentCommand(
                "CV Extraction Agent", "CV_EXTRACTION_AGENT", "EXTRACTION",
                null, deploymentId, null);

        when(agentRepository.existsByCode(any())).thenReturn(false);
        when(deploymentRepository.findById(deploymentId)).thenReturn(Optional.of(inactiveDeployment(deploymentId)));

        assertThatThrownBy(() -> service.createAgent(command))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("ACTIVE")
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus())
                        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));

        verify(agentRepository, never()).save(any());
    }

    @Test
    void createAgent_withMissingDeployment_throwsAppExceptionWithNotFound() {
        UUID deploymentId = UUID.randomUUID();
        CreateAgentCommand command = new CreateAgentCommand(
                "CV Extraction Agent", "CV_EXTRACTION_AGENT", "EXTRACTION",
                null, deploymentId, null);

        when(agentRepository.existsByCode(any())).thenReturn(false);
        when(deploymentRepository.findById(deploymentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createAgent(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND));

        verify(agentRepository, never()).save(any());
    }

    // --- helpers ---

    private ModelDeployment activeDeployment(UUID id) {
        return ModelDeployment.reconstitute(id, UUID.randomUUID(), "GPT-4.1 Prod",
                ModelDeploymentCode.of("GPT_4_1_PROD"), ModelDeploymentEnvironment.PROD,
                null, null, new BigDecimal("0.7"), null, false, null,
                ModelDeploymentStatus.ACTIVE, Instant.now(), Instant.now());
    }

    private ModelDeployment inactiveDeployment(UUID id) {
        return ModelDeployment.reconstitute(id, UUID.randomUUID(), "GPT-4.1 UAT",
                ModelDeploymentCode.of("GPT_4_1_UAT"), ModelDeploymentEnvironment.UAT,
                null, null, new BigDecimal("0.5"), null, false, null,
                ModelDeploymentStatus.INACTIVE, Instant.now(), Instant.now());
    }
}
