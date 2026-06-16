package com.company.scopery.modules.aiagent.execution.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.aiagent.agent.domain.AgentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.eventconfig.domain.EventConfigRepository;
import com.company.scopery.modules.aiagent.execution.application.command.*;
import com.company.scopery.modules.aiagent.execution.application.response.ExecutionLogDetailResponse;
import com.company.scopery.modules.aiagent.execution.application.response.ExecutionLogResponse;
import com.company.scopery.modules.aiagent.execution.domain.*;
import com.company.scopery.modules.aiagent.prompt.domain.PromptVersionRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinitionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecutionLogApplicationServiceTest {

    @Mock private ExecutionLogRepository executionLogRepository;
    @Mock private EventConfigRepository eventConfigRepository;
    @Mock private EventDefinitionRepository eventDefinitionRepository;
    @Mock private AgentRepository agentRepository;
    @Mock private PromptVersionRepository promptVersionRepository;
    @Mock private ModelDeploymentRepository modelDeploymentRepository;
    @Mock private AiAgentActivityLogger activityLogger;

    private ExecutionLogApplicationService service;
    private final UUID agentId = UUID.randomUUID();
    private final UUID promptVersionId = UUID.randomUUID();
    private final UUID modelDeploymentId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new ExecutionLogApplicationService(executionLogRepository, eventConfigRepository,
                eventDefinitionRepository, agentRepository, promptVersionRepository,
                modelDeploymentRepository, activityLogger, new ObjectMapper());
    }

    @Test
    void createExecutionLog_succeeds() {
        CreateExecutionLogCommand command = buildCreateCommand("req-001");

        when(executionLogRepository.existsByRequestId(ExecutionRequestId.of("req-001"))).thenReturn(false);
        when(agentRepository.findById(agentId)).thenReturn(Optional.of(mock(com.company.scopery.modules.aiagent.agent.domain.Agent.class)));
        when(promptVersionRepository.findById(promptVersionId)).thenReturn(Optional.of(mock(com.company.scopery.modules.aiagent.prompt.domain.PromptVersion.class)));
        when(modelDeploymentRepository.findById(modelDeploymentId)).thenReturn(Optional.of(mock(com.company.scopery.modules.aiagent.deployment.domain.ModelDeployment.class)));
        when(executionLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ExecutionLogResponse response = service.createExecutionLog(command);

        assertThat(response.requestId()).isEqualTo("req-001");
        assertThat(response.status()).isEqualTo("PENDING");
        assertThat(response.triggerSource()).isEqualTo("EVENT");
        verify(executionLogRepository).save(any());
    }

    @Test
    void createExecutionLog_duplicateRequestId_throwsConflict() {
        CreateExecutionLogCommand command = buildCreateCommand("req-duplicate");

        when(executionLogRepository.existsByRequestId(ExecutionRequestId.of("req-duplicate"))).thenReturn(true);

        assertThatThrownBy(() -> service.createExecutionLog(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode())
                            .isEqualTo(AiAgentErrorCatalog.EXECUTION_LOG_REQUEST_ID_ALREADY_EXISTS.code());
                });

        verify(executionLogRepository, never()).save(any());
    }

    @Test
    void createExecutionLog_agentNotFound_throwsNotFound() {
        CreateExecutionLogCommand command = buildCreateCommand("req-002");

        when(executionLogRepository.existsByRequestId(any())).thenReturn(false);
        when(agentRepository.findById(agentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createExecutionLog(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND));

        verify(executionLogRepository, never()).save(any());
    }

    @Test
    void createExecutionLog_invalidMetadataJson_throwsBadRequest() {
        CreateExecutionLogCommand command = new CreateExecutionLogCommand(
                "req-003", null, null, agentId, promptVersionId, modelDeploymentId,
                "EVENT", "not-valid-json{{{");

        when(executionLogRepository.existsByRequestId(any())).thenReturn(false);

        assertThatThrownBy(() -> service.createExecutionLog(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ae.getErrorCode()).isEqualTo(AiAgentErrorCatalog.INVALID_EXECUTION_METADATA_JSON.code());
                });
    }

    @Test
    void markRunning_fromPending_succeeds() {
        UUID id = UUID.randomUUID();
        ExecutionLog pending = buildPendingLog(id);

        when(executionLogRepository.findById(id)).thenReturn(Optional.of(pending));
        when(executionLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(agentRepository.findById(any())).thenReturn(Optional.empty());
        when(promptVersionRepository.findById(any())).thenReturn(Optional.empty());
        when(modelDeploymentRepository.findById(any())).thenReturn(Optional.empty());

        ExecutionLogDetailResponse response = service.markRunning(new MarkExecutionRunningCommand(id));

        assertThat(response.status()).isEqualTo("RUNNING");
    }

    @Test
    void markRunning_fromTerminal_throwsUnprocessable() {
        UUID id = UUID.randomUUID();
        ExecutionLog cancelled = buildLogWithStatus(id, ExecutionStatus.CANCELLED);

        when(executionLogRepository.findById(id)).thenReturn(Optional.of(cancelled));

        assertThatThrownBy(() -> service.markRunning(new MarkExecutionRunningCommand(id)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode())
                            .isEqualTo(AiAgentErrorCatalog.TERMINAL_EXECUTION_LOG_CANNOT_BE_UPDATED.code());
                });
    }

    @Test
    void markSucceeded_invalidTransition_throwsUnprocessable() {
        UUID id = UUID.randomUUID();
        ExecutionLog pending = buildPendingLog(id);

        when(executionLogRepository.findById(id)).thenReturn(Optional.of(pending));

        assertThatThrownBy(() -> service.markSucceeded(new MarkExecutionSucceededCommand(
                id, null, null, null, null, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode())
                            .isEqualTo(AiAgentErrorCatalog.INVALID_EXECUTION_STATUS_TRANSITION.code());
                });
    }

    @Test
    void cancel_fromRunning_succeeds() {
        UUID id = UUID.randomUUID();
        ExecutionLog running = buildLogWithStatus(id, ExecutionStatus.RUNNING);

        when(executionLogRepository.findById(id)).thenReturn(Optional.of(running));
        when(executionLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(agentRepository.findById(any())).thenReturn(Optional.empty());
        when(promptVersionRepository.findById(any())).thenReturn(Optional.empty());
        when(modelDeploymentRepository.findById(any())).thenReturn(Optional.empty());

        ExecutionLogDetailResponse response = service.cancelExecution(new CancelExecutionCommand(id));

        assertThat(response.status()).isEqualTo("CANCELLED");
    }

    // --- helpers ---

    private CreateExecutionLogCommand buildCreateCommand(String requestId) {
        return new CreateExecutionLogCommand(requestId, null, null,
                agentId, promptVersionId, modelDeploymentId, "EVENT", null);
    }

    private ExecutionLog buildPendingLog(UUID id) {
        return ExecutionLog.reconstitute(id, ExecutionRequestId.of("req-" + id),
                null, null, agentId, promptVersionId, modelDeploymentId,
                ExecutionTriggerSource.EVENT, ExecutionStatus.PENDING,
                null, null, null, null, null, null, null, null, null, null, null,
                Instant.now(), Instant.now());
    }

    private ExecutionLog buildLogWithStatus(UUID id, ExecutionStatus status) {
        Instant startedAt = status == ExecutionStatus.RUNNING ? Instant.now() : null;
        return ExecutionLog.reconstitute(id, ExecutionRequestId.of("req-" + id),
                null, null, agentId, promptVersionId, modelDeploymentId,
                ExecutionTriggerSource.EVENT, status,
                startedAt, null, null, null, null, null, null, null, null, null, null,
                Instant.now(), Instant.now());
    }
}