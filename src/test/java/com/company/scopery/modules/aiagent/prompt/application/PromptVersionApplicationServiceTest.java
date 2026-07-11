package com.company.scopery.modules.aiagent.prompt.application;
import com.company.scopery.modules.aiagent.prompt.application.action.ActivatePromptVersionAction;
import com.company.scopery.modules.aiagent.prompt.application.action.CreatePromptVersionAction;
import com.company.scopery.modules.aiagent.prompt.application.action.UpdatePromptVersionAction;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.aiagent.prompt.application.command.ActivatePromptVersionCommand;
import com.company.scopery.modules.aiagent.prompt.application.command.CreatePromptVersionCommand;
import com.company.scopery.modules.aiagent.prompt.application.command.UpdatePromptVersionCommand;
import com.company.scopery.modules.aiagent.prompt.application.response.PromptVersionDetailResponse;
import com.company.scopery.modules.aiagent.prompt.application.response.PromptVersionResponse;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptContentFormat;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptTemplateStatus;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptVersionStatus;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplate;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplateRepository;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersionRepository;
import com.company.scopery.modules.aiagent.prompt.domain.valueobject.PromptTemplateCode;
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
class PromptVersionActionTest {

    @Mock private PromptVersionRepository versionRepository;
    @Mock private PromptTemplateRepository templateRepository;
    @Mock private AiAgentActivityLogger activityLogger;


    private ActivatePromptVersionAction activatePromptVersionAction;
    private CreatePromptVersionAction createPromptVersionAction;
    private UpdatePromptVersionAction updatePromptVersionAction;

    @BeforeEach
    void setUp() {
        activatePromptVersionAction = new ActivatePromptVersionAction(versionRepository, templateRepository, activityLogger);
        createPromptVersionAction = new CreatePromptVersionAction(versionRepository, templateRepository, activityLogger);
        updatePromptVersionAction = new UpdatePromptVersionAction(versionRepository, templateRepository, activityLogger);
    }

    @Test
    void createPromptVersion_firstVersion_versionNumberIsOne() {
        UUID templateId = UUID.randomUUID();
        CreatePromptVersionCommand command = new CreatePromptVersionCommand(
                templateId, "v1 Title", "Extract CV data", "TEXT", null, "Initial version");

        when(templateRepository.findById(templateId)).thenReturn(Optional.of(activeTemplate(templateId)));
        when(versionRepository.getMaxVersionNumber(templateId)).thenReturn(0);
        when(versionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PromptVersionResponse response = createPromptVersionAction.execute(command);

        assertThat(response.versionNumber()).isEqualTo(1);
        assertThat(response.status()).isEqualTo("DRAFT");
        assertThat(response.contentFormat()).isEqualTo("TEXT");
    }

    @Test
    void createPromptVersion_secondVersion_versionNumberIsTwo() {
        UUID templateId = UUID.randomUUID();
        CreatePromptVersionCommand command = new CreatePromptVersionCommand(
                templateId, "v2 Title", "Improved CV extraction", "MARKDOWN", null, "Improved");

        when(templateRepository.findById(templateId)).thenReturn(Optional.of(activeTemplate(templateId)));
        when(versionRepository.getMaxVersionNumber(templateId)).thenReturn(1);
        when(versionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PromptVersionResponse response = createPromptVersionAction.execute(command);

        assertThat(response.versionNumber()).isEqualTo(2);
    }

    @Test
    void createPromptVersion_deprecatedTemplate_throwsAppExceptionWithUnprocessable() {
        UUID templateId = UUID.randomUUID();
        CreatePromptVersionCommand command = new CreatePromptVersionCommand(
                templateId, null, "Some content", "TEXT", null, null);

        when(templateRepository.findById(templateId)).thenReturn(Optional.of(deprecatedTemplate(templateId)));

        assertThatThrownBy(() -> createPromptVersionAction.execute(command))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("deprecated")
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            AiAgentErrorCatalog.PROMPT_VERSION_TEMPLATE_DEPRECATED.code());
                });

        verify(versionRepository, never()).save(any());
    }

    @Test
    void createPromptVersion_templateNotFound_throwsAppExceptionWithNotFound() {
        UUID templateId = UUID.randomUUID();
        CreatePromptVersionCommand command = new CreatePromptVersionCommand(
                templateId, null, "Content", "TEXT", null, null);

        when(templateRepository.findById(templateId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createPromptVersionAction.execute(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void updatePromptVersion_whenNotDraft_throwsAppExceptionWithUnprocessable() {
        UUID versionId = UUID.randomUUID();
        PromptVersion activeVersion = PromptVersion.reconstitute(versionId, UUID.randomUUID(), 1,
                null, "Content", PromptContentFormat.TEXT, null, null,
                PromptVersionStatus.ACTIVE, Instant.now(), Instant.now());

        when(versionRepository.findById(versionId)).thenReturn(Optional.of(activeVersion));

        UpdatePromptVersionCommand command = new UpdatePromptVersionCommand(
                versionId, null, "New content", "TEXT", null, null);

        assertThatThrownBy(() -> updatePromptVersionAction.execute(command))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("DRAFT")
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            AiAgentErrorCatalog.PROMPT_VERSION_CONTENT_NOT_EDITABLE.code());
                });
    }

    @Test
    void activatePromptVersion_archivedVersion_throwsAppExceptionWithUnprocessable() {
        UUID versionId = UUID.randomUUID();
        PromptVersion archived = PromptVersion.reconstitute(versionId, UUID.randomUUID(), 1,
                null, "Content", PromptContentFormat.TEXT, null, null,
                PromptVersionStatus.ARCHIVED, Instant.now(), Instant.now());

        when(versionRepository.findById(versionId)).thenReturn(Optional.of(archived));

        assertThatThrownBy(() -> activatePromptVersionAction.execute(new ActivatePromptVersionCommand(versionId)))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Archived")
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            AiAgentErrorCatalog.ARCHIVED_PROMPT_VERSION_CANNOT_BE_ACTIVATED.code());
                });
    }

    @Test
    void activatePromptVersion_inactiveTemplate_throwsAppExceptionWithUnprocessable() {
        UUID templateId = UUID.randomUUID();
        UUID versionId = UUID.randomUUID();
        PromptVersion draft = PromptVersion.reconstitute(versionId, templateId, 1,
                null, "Content", PromptContentFormat.TEXT, null, null,
                PromptVersionStatus.DRAFT, Instant.now(), Instant.now());

        when(versionRepository.findById(versionId)).thenReturn(Optional.of(draft));
        when(templateRepository.findById(templateId)).thenReturn(Optional.of(inactiveTemplate(templateId)));

        assertThatThrownBy(() -> activatePromptVersionAction.execute(new ActivatePromptVersionCommand(versionId)))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("ACTIVE")
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            AiAgentErrorCatalog.PROMPT_VERSION_TEMPLATE_NOT_ACTIVE.code());
                });
    }

    // --- helpers ---

    private PromptTemplate activeTemplate(UUID id) {
        return PromptTemplate.reconstitute(id, UUID.randomUUID(), "CV Extraction Prompt",
                PromptTemplateCode.of("CV_EXTRACTION_PROMPT"), null,
                PromptTemplateStatus.ACTIVE, Instant.now(), Instant.now());
    }

    private PromptTemplate deprecatedTemplate(UUID id) {
        return PromptTemplate.reconstitute(id, UUID.randomUUID(), "Old Prompt",
                PromptTemplateCode.of("OLD_PROMPT"), null,
                PromptTemplateStatus.DEPRECATED, Instant.now(), Instant.now());
    }

    private PromptTemplate inactiveTemplate(UUID id) {
        return PromptTemplate.reconstitute(id, UUID.randomUUID(), "Inactive Prompt",
                PromptTemplateCode.of("INACTIVE_PROMPT"), null,
                PromptTemplateStatus.INACTIVE, Instant.now(), Instant.now());
    }
}
