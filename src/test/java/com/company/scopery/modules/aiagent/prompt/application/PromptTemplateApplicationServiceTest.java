package com.company.scopery.modules.aiagent.prompt.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.aiagent.agent.domain.*;
import com.company.scopery.modules.aiagent.prompt.application.command.CreatePromptTemplateCommand;
import com.company.scopery.modules.aiagent.prompt.application.command.ActivatePromptTemplateCommand;
import com.company.scopery.modules.aiagent.prompt.application.response.PromptTemplateResponse;
import com.company.scopery.modules.aiagent.prompt.application.response.PromptTemplateDetailResponse;
import com.company.scopery.modules.aiagent.prompt.domain.*;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
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
class PromptTemplateApplicationServiceTest {

    @Mock private PromptTemplateRepository templateRepository;
    @Mock private AgentRepository agentRepository;
    @Mock private AiAgentActivityLogger activityLogger;

    private PromptTemplateApplicationService service;

    @BeforeEach
    void setUp() {
        service = new PromptTemplateApplicationService(templateRepository, agentRepository, activityLogger);
    }

    @Test
    void createPromptTemplate_success_returnsActiveResponse() {
        UUID agentId = UUID.randomUUID();
        CreatePromptTemplateCommand command = new CreatePromptTemplateCommand(
                agentId, "CV Extraction Prompt", "CV_EXTRACTION_PROMPT", null);

        when(agentRepository.findById(agentId)).thenReturn(Optional.of(activeAgent(agentId)));
        when(templateRepository.existsByAgentIdAndCode(eq(agentId), any())).thenReturn(false);
        when(templateRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PromptTemplateResponse response = service.createPromptTemplate(command);

        assertThat(response.code()).isEqualTo("CV_EXTRACTION_PROMPT");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.agentId()).isEqualTo(agentId);
        verify(templateRepository).save(any());
        verify(activityLogger).logSuccess(eq("PROMPT_TEMPLATE"), any(UUID.class),
                eq("CREATE_PROMPT_TEMPLATE"), any(String.class));
    }

    @Test
    void createPromptTemplate_duplicateCodeUnderSameAgent_throwsAppExceptionWithConflict() {
        UUID agentId = UUID.randomUUID();
        CreatePromptTemplateCommand command = new CreatePromptTemplateCommand(
                agentId, "CV Extraction Prompt", "CV_EXTRACTION_PROMPT", null);

        when(agentRepository.findById(agentId)).thenReturn(Optional.of(activeAgent(agentId)));
        when(templateRepository.existsByAgentIdAndCode(eq(agentId), any())).thenReturn(true);

        assertThatThrownBy(() -> service.createPromptTemplate(command))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("CV_EXTRACTION_PROMPT")
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            AiAgentErrorCatalog.PROMPT_TEMPLATE_CODE_ALREADY_EXISTS.code());
                });

        verify(templateRepository, never()).save(any());
    }

    @Test
    void createPromptTemplate_deprecatedAgent_throwsAppExceptionWithUnprocessable() {
        UUID agentId = UUID.randomUUID();
        CreatePromptTemplateCommand command = new CreatePromptTemplateCommand(
                agentId, "CV Extraction Prompt", "CV_EXTRACTION_PROMPT", null);

        when(agentRepository.findById(agentId)).thenReturn(Optional.of(deprecatedAgent(agentId)));

        assertThatThrownBy(() -> service.createPromptTemplate(command))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("deprecated")
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus())
                        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY));

        verify(templateRepository, never()).save(any());
    }

    @Test
    void createPromptTemplate_agentNotFound_throwsAppExceptionWithNotFound() {
        UUID agentId = UUID.randomUUID();
        CreatePromptTemplateCommand command = new CreatePromptTemplateCommand(
                agentId, "CV Extraction Prompt", "CV_EXTRACTION_PROMPT", null);

        when(agentRepository.findById(agentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createPromptTemplate(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void activatePromptTemplate_deprecated_throwsAppExceptionWithUnprocessable() {
        UUID templateId = UUID.randomUUID();
        PromptTemplate deprecated = PromptTemplate.reconstitute(templateId, UUID.randomUUID(),
                "Old Prompt", PromptTemplateCode.of("OLD_PROMPT"), null,
                PromptTemplateStatus.DEPRECATED, Instant.now(), Instant.now());

        when(templateRepository.findById(templateId)).thenReturn(Optional.of(deprecated));

        assertThatThrownBy(() -> service.activatePromptTemplate(new ActivatePromptTemplateCommand(templateId)))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Deprecated")
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            AiAgentErrorCatalog.DEPRECATED_PROMPT_TEMPLATE_CANNOT_BE_ACTIVATED.code());
                });
    }

    // --- helpers ---

    private Agent activeAgent(UUID id) {
        return Agent.reconstitute(id, "CV Extraction Agent", AgentCode.of("CV_EXTRACTION_AGENT"),
                AgentType.EXTRACTION, null, null, null,
                AgentStatus.ACTIVE, Instant.now(), Instant.now());
    }

    private Agent deprecatedAgent(UUID id) {
        return Agent.reconstitute(id, "Old Agent", AgentCode.of("OLD_AGENT"),
                AgentType.OTHER, null, null, null,
                AgentStatus.DEPRECATED, Instant.now(), Instant.now());
    }
}
