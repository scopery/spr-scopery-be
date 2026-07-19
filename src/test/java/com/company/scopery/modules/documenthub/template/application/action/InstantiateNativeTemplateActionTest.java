package com.company.scopery.modules.documenthub.template.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.documenthub.nativecontent.application.action.SaveDocumentContentAction;
import com.company.scopery.modules.documenthub.nativecontent.application.command.SaveDocumentContentCommand;
import com.company.scopery.modules.documenthub.nativecontent.application.response.DocumentContentResponse;
import com.company.scopery.modules.documenthub.nativecontent.domain.enums.RevisionType;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubErrorCatalog;
import com.company.scopery.modules.documenthub.template.application.command.InstantiateNativeTemplateCommand;
import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateVariable;
import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateVariableRepository;
import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateVersion;
import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateVersionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests BR-NDE-043: template variable substitution only for non-sensitive variables.
 * Tests BR-NDE-044: sensitive variable not substituted (placeholder remains).
 */
@ExtendWith(MockitoExtension.class)
class InstantiateNativeTemplateActionTest {

    @Mock DocumentTemplateVersionRepository versionRepo;
    @Mock DocumentTemplateVariableRepository variableRepo;
    @Mock SaveDocumentContentAction saveContent;
    @Mock DocumentHubAuthorizationService authorization;

    InstantiateNativeTemplateAction action;

    final UUID workspaceId = UUID.randomUUID();
    final UUID projectId = UUID.randomUUID();
    final UUID templateId = UUID.randomUUID();
    final UUID templateVersionId = UUID.randomUUID();
    final UUID targetDocumentId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        action = new InstantiateNativeTemplateAction(versionRepo, variableRepo, saveContent, authorization);
    }

    @Test
    void execute_substitutesNonSensitiveVariables() {
        // BR-NDE-043: server-side variable substitution for whitelist keys
        String ast = "Hello {{projectName}}, created by {{author}}";
        DocumentTemplateVersion version = new DocumentTemplateVersion(templateVersionId, templateId, 1,
                null, "NATIVE", "PUBLISHED", ast);
        when(versionRepo.findByIdAndTemplateId(templateVersionId, templateId)).thenReturn(Optional.of(version));

        DocumentTemplateVariable varProject = variable(templateVersionId, "projectName", false, true);
        DocumentTemplateVariable varAuthor = variable(templateVersionId, "author", false, true);
        when(variableRepo.findByTemplateVersionId(templateVersionId)).thenReturn(List.of(varProject, varAuthor));

        DocumentContentResponse stubResponse = stubContentResponse();
        when(saveContent.execute(any())).thenReturn(stubResponse);

        var cmd = new InstantiateNativeTemplateCommand(workspaceId, templateId, templateVersionId,
                projectId, targetDocumentId, Map.of("projectName", "Scopery", "author", "Alice"));
        action.execute(cmd);

        ArgumentCaptor<SaveDocumentContentCommand> captor = ArgumentCaptor.forClass(SaveDocumentContentCommand.class);
        verify(saveContent).execute(captor.capture());
        String resolvedAst = captor.getValue().ast();

        assertThat(resolvedAst).contains("Scopery");
        assertThat(resolvedAst).contains("Alice");
        assertThat(resolvedAst).doesNotContain("{{projectName}}");
        assertThat(resolvedAst).doesNotContain("{{author}}");
    }

    @Test
    void execute_doesNotSubstituteSensitiveVariables() {
        // BR-NDE-044: sensitive variable placeholder remains untouched
        String ast = "Budget: {{budget}}, Team: {{teamName}}";
        DocumentTemplateVersion version = new DocumentTemplateVersion(templateVersionId, templateId, 1,
                null, "NATIVE", "PUBLISHED", ast);
        when(versionRepo.findByIdAndTemplateId(templateVersionId, templateId)).thenReturn(Optional.of(version));

        DocumentTemplateVariable sensitiveVar = variable(templateVersionId, "budget", true /* sensitive */, true);
        DocumentTemplateVariable normalVar = variable(templateVersionId, "teamName", false, true);
        when(variableRepo.findByTemplateVersionId(templateVersionId)).thenReturn(List.of(sensitiveVar, normalVar));

        when(saveContent.execute(any())).thenReturn(stubContentResponse());

        var cmd = new InstantiateNativeTemplateCommand(workspaceId, templateId, templateVersionId,
                projectId, targetDocumentId, Map.of("budget", "1000000", "teamName", "DevOps"));
        action.execute(cmd);

        ArgumentCaptor<SaveDocumentContentCommand> captor = ArgumentCaptor.forClass(SaveDocumentContentCommand.class);
        verify(saveContent).execute(captor.capture());
        String resolvedAst = captor.getValue().ast();

        // Sensitive variable NOT substituted
        assertThat(resolvedAst).contains("{{budget}}");
        // Non-sensitive variable IS substituted
        assertThat(resolvedAst).contains("DevOps");
        assertThat(resolvedAst).doesNotContain("{{teamName}}");
    }

    @Test
    void execute_throwsVariableMissing_whenRequiredVariableNotProvided() {
        String ast = "Hello {{projectName}}";
        DocumentTemplateVersion version = new DocumentTemplateVersion(templateVersionId, templateId, 1,
                null, "NATIVE", "PUBLISHED", ast);
        when(versionRepo.findByIdAndTemplateId(templateVersionId, templateId)).thenReturn(Optional.of(version));

        DocumentTemplateVariable requiredVar = variable(templateVersionId, "projectName", false, true /* required */);
        when(variableRepo.findByTemplateVersionId(templateVersionId)).thenReturn(List.of(requiredVar));

        // Provide empty variables map — required "projectName" is missing
        var cmd = new InstantiateNativeTemplateCommand(workspaceId, templateId, templateVersionId,
                projectId, targetDocumentId, Map.of());

        assertThatThrownBy(() -> action.execute(cmd))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.TEMPLATE_VARIABLE_MISSING.code()));

        verify(saveContent, never()).execute(any());
    }

    @Test
    void execute_throwsVariableMissing_whenVariablesMapIsNull() {
        String ast = "Hello {{projectName}}";
        DocumentTemplateVersion version = new DocumentTemplateVersion(templateVersionId, templateId, 1,
                null, "NATIVE", "PUBLISHED", ast);
        when(versionRepo.findByIdAndTemplateId(templateVersionId, templateId)).thenReturn(Optional.of(version));

        DocumentTemplateVariable requiredVar = variable(templateVersionId, "projectName", false, true);
        when(variableRepo.findByTemplateVersionId(templateVersionId)).thenReturn(List.of(requiredVar));

        var cmd = new InstantiateNativeTemplateCommand(workspaceId, templateId, templateVersionId,
                projectId, targetDocumentId, null);

        assertThatThrownBy(() -> action.execute(cmd))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.TEMPLATE_VARIABLE_MISSING.code()));
    }

    @Test
    void execute_noValidationError_whenOptionalVariableNotProvided() {
        String ast = "Hello {{optionalNote}}";
        DocumentTemplateVersion version = new DocumentTemplateVersion(templateVersionId, templateId, 1,
                null, "NATIVE", "PUBLISHED", ast);
        when(versionRepo.findByIdAndTemplateId(templateVersionId, templateId)).thenReturn(Optional.of(version));

        // required = false → optional variable
        DocumentTemplateVariable optionalVar = variable(templateVersionId, "optionalNote", false, false /* not required */);
        when(variableRepo.findByTemplateVersionId(templateVersionId)).thenReturn(List.of(optionalVar));
        when(saveContent.execute(any())).thenReturn(stubContentResponse());

        // No variables provided for optional var — should not throw
        var cmd = new InstantiateNativeTemplateCommand(workspaceId, templateId, templateVersionId,
                projectId, targetDocumentId, Map.of());

        action.execute(cmd); // No exception
        verify(saveContent).execute(any());
    }

    @Test
    void execute_usesTemplateCreateRevisionType() {
        String ast = "Hello {{name}}";
        DocumentTemplateVersion version = new DocumentTemplateVersion(templateVersionId, templateId, 1,
                null, "NATIVE", "PUBLISHED", ast);
        when(versionRepo.findByIdAndTemplateId(templateVersionId, templateId)).thenReturn(Optional.of(version));

        DocumentTemplateVariable var = variable(templateVersionId, "name", false, true);
        when(variableRepo.findByTemplateVersionId(templateVersionId)).thenReturn(List.of(var));
        when(saveContent.execute(any())).thenReturn(stubContentResponse());

        var cmd = new InstantiateNativeTemplateCommand(workspaceId, templateId, templateVersionId,
                projectId, targetDocumentId, Map.of("name", "World"));
        action.execute(cmd);

        ArgumentCaptor<SaveDocumentContentCommand> captor = ArgumentCaptor.forClass(SaveDocumentContentCommand.class);
        verify(saveContent).execute(captor.capture());
        assertThat(captor.getValue().revisionType()).isEqualTo(RevisionType.TEMPLATE_CREATE);
    }

    @Test
    void execute_throwsNotFound_whenTemplateVersionMissing() {
        when(versionRepo.findByIdAndTemplateId(templateVersionId, templateId)).thenReturn(Optional.empty());

        var cmd = new InstantiateNativeTemplateCommand(workspaceId, templateId, templateVersionId,
                projectId, targetDocumentId, Map.of());

        assertThatThrownBy(() -> action.execute(cmd))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.TEMPLATE_NOT_FOUND.code()));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * @param sensitive   whether the variable is sensitive
     * @param required    whether the variable is required
     */
    private DocumentTemplateVariable variable(UUID versionId, String key, boolean sensitive, boolean required) {
        return new DocumentTemplateVariable(UUID.randomUUID(), versionId, key, key + " label",
                "TEXT", required, null, sensitive, 0, Instant.now(), Instant.now());
    }

    private DocumentContentResponse stubContentResponse() {
        return new DocumentContentResponse(UUID.randomUUID(), targetDocumentId, 1L,
                "resolved-ast", "ck", 1, 1, 8, Instant.now(), null, Instant.now(), Instant.now());
    }
}
