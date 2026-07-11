package com.company.scopery.modules.aiagent.usagepolicy.application;
import com.company.scopery.modules.aiagent.usagepolicy.application.action.ActivateUsagePolicyAction;
import com.company.scopery.modules.aiagent.usagepolicy.application.action.CreateUsagePolicyAction;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfigRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.usagepolicy.application.command.ActivateUsagePolicyCommand;
import com.company.scopery.modules.aiagent.usagepolicy.application.command.CreateUsagePolicyCommand;
import com.company.scopery.modules.aiagent.usagepolicy.application.response.UsagePolicyResponse;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyAction;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyPeriod;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyStatus;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyTargetType;
import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicy;
import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicyRepository;
import com.company.scopery.modules.aiagent.usagepolicy.domain.valueobject.UsagePolicyCode;
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
class UsagePolicyActionTest {

    @Mock private UsagePolicyRepository usagePolicyRepository;
    @Mock private EventConfigRepository eventConfigRepository;
    @Mock private AgentRepository agentRepository;
    @Mock private ModelDeploymentRepository modelDeploymentRepository;
    @Mock private AiAgentActivityLogger activityLogger;


    private ActivateUsagePolicyAction activateUsagePolicyAction;
    private CreateUsagePolicyAction createUsagePolicyAction;

    @BeforeEach
    void setUp() {
        activateUsagePolicyAction = new ActivateUsagePolicyAction(usagePolicyRepository, eventConfigRepository, agentRepository, modelDeploymentRepository, activityLogger);
        createUsagePolicyAction = new CreateUsagePolicyAction(usagePolicyRepository, eventConfigRepository, agentRepository, modelDeploymentRepository, activityLogger);
    }

    @Test
    void createUsagePolicy_global_succeeds() {
        CreateUsagePolicyCommand command = globalCommand("GLOBAL_LIMIT", null);

        when(usagePolicyRepository.existsByCode(UsagePolicyCode.of("GLOBAL_LIMIT"))).thenReturn(false);
        when(usagePolicyRepository.existsActiveByTargetTypeAndTargetId(any(), any(), any())).thenReturn(false);
        when(usagePolicyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UsagePolicyResponse response = createUsagePolicyAction.execute(command);

        assertThat(response.code()).isEqualTo("GLOBAL_LIMIT");
        assertThat(response.status()).isEqualTo("INACTIVE");
        assertThat(response.targetType()).isEqualTo("GLOBAL");
        assertThat(response.targetId()).isNull();
        verify(usagePolicyRepository).save(any());
    }

    @Test
    void createUsagePolicy_normalizesCodeToUppercase() {
        CreateUsagePolicyCommand command = globalCommand("global_limit", null);

        when(usagePolicyRepository.existsByCode(UsagePolicyCode.of("GLOBAL_LIMIT"))).thenReturn(false);
        when(usagePolicyRepository.existsActiveByTargetTypeAndTargetId(any(), any(), any())).thenReturn(false);
        when(usagePolicyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UsagePolicyResponse response = createUsagePolicyAction.execute(command);

        assertThat(response.code()).isEqualTo("GLOBAL_LIMIT");
    }

    @Test
    void createUsagePolicy_duplicateCode_throwsConflict() {
        CreateUsagePolicyCommand command = globalCommand("GLOBAL_LIMIT", null);

        when(usagePolicyRepository.existsByCode(UsagePolicyCode.of("GLOBAL_LIMIT"))).thenReturn(true);

        assertThatThrownBy(() -> createUsagePolicyAction.execute(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(AiAgentErrorCatalog.USAGE_POLICY_CODE_ALREADY_EXISTS.code());
                });

        verify(usagePolicyRepository, never()).save(any());
    }

    @Test
    void createUsagePolicy_globalWithTargetId_throwsBadRequest() {
        CreateUsagePolicyCommand command = new CreateUsagePolicyCommand(
                "GLOBAL_LIMIT", "Global Limit", "GLOBAL", UUID.randomUUID(),
                1000, null, null, null, null, "DAY", "BLOCK", 100, null);

        when(usagePolicyRepository.existsByCode(any())).thenReturn(false);

        assertThatThrownBy(() -> createUsagePolicyAction.execute(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST));

        verify(usagePolicyRepository, never()).save(any());
    }

    @Test
    void createUsagePolicy_nonGlobalWithoutTargetId_throwsBadRequest() {
        CreateUsagePolicyCommand command = new CreateUsagePolicyCommand(
                "AGENT_LIMIT", "Agent Limit", "AGENT", null,
                500, null, null, null, null, "HOUR", "WARN", 100, null);

        when(usagePolicyRepository.existsByCode(any())).thenReturn(false);

        assertThatThrownBy(() -> createUsagePolicyAction.execute(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST));

        verify(usagePolicyRepository, never()).save(any());
    }

    @Test
    void createUsagePolicy_noLimitDefined_throwsUnprocessable() {
        CreateUsagePolicyCommand command = new CreateUsagePolicyCommand(
                "GLOBAL_LIMIT", "Global Limit", "GLOBAL", null,
                null, null, null, null, null, null, "BLOCK", 100, null);

        when(usagePolicyRepository.existsByCode(any())).thenReturn(false);

        assertThatThrownBy(() -> createUsagePolicyAction.execute(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));

        verify(usagePolicyRepository, never()).save(any());
    }

    @Test
    void createUsagePolicy_periodBasedLimitWithoutPeriod_throwsUnprocessable() {
        CreateUsagePolicyCommand command = new CreateUsagePolicyCommand(
                "GLOBAL_LIMIT", "Global Limit", "GLOBAL", null,
                1000, null, null, null, null, null, "BLOCK", 100, null);

        when(usagePolicyRepository.existsByCode(any())).thenReturn(false);

        assertThatThrownBy(() -> createUsagePolicyAction.execute(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(AiAgentErrorCatalog.USAGE_POLICY_PERIOD_REQUIRED.code());
                });
    }

    @Test
    void createUsagePolicy_activeAlreadyExists_throwsConflict() {
        CreateUsagePolicyCommand command = globalCommand("GLOBAL_LIMIT", null);

        when(usagePolicyRepository.existsByCode(any())).thenReturn(false);
        when(usagePolicyRepository.existsActiveByTargetTypeAndTargetId(
                UsagePolicyTargetType.GLOBAL, null, null)).thenReturn(true);

        assertThatThrownBy(() -> createUsagePolicyAction.execute(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.CONFLICT));

        verify(usagePolicyRepository, never()).save(any());
    }

    @Test
    void activateUsagePolicy_deprecated_throwsUnprocessable() {
        UUID id = UUID.randomUUID();
        UsagePolicy deprecated = UsagePolicy.reconstitute(id, UsagePolicyCode.of("OLD_POLICY"), "Old",
                UsagePolicyTargetType.GLOBAL, null, 1000, null, null, null, null,
                UsagePolicyPeriod.DAY, UsagePolicyAction.BLOCK, 100, null,
                UsagePolicyStatus.DEPRECATED, Instant.now(), Instant.now());

        when(usagePolicyRepository.findById(id)).thenReturn(Optional.of(deprecated));

        assertThatThrownBy(() -> activateUsagePolicyAction.execute(new ActivateUsagePolicyCommand(id)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode())
                            .isEqualTo(AiAgentErrorCatalog.DEPRECATED_USAGE_POLICY_CANNOT_BE_ACTIVATED.code());
                });
    }

    // --- helpers ---

    private CreateUsagePolicyCommand globalCommand(String code, Integer priority) {
        return new CreateUsagePolicyCommand(code, "Global Limit", "GLOBAL", null,
                1000, null, null, null, null, "DAY", "BLOCK",
                priority != null ? priority : 100, null);
    }
}
