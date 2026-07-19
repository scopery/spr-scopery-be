package com.company.scopery.modules.knowledge.documenttype.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.knowledge.documenttype.application.action.ActivateDocumentTypeAction;
import com.company.scopery.modules.knowledge.documenttype.application.action.ArchiveDocumentTypeAction;
import com.company.scopery.modules.knowledge.documenttype.application.action.CreateDocumentTypeAction;
import com.company.scopery.modules.knowledge.documenttype.application.action.CreateSystemDocumentTypeAction;
import com.company.scopery.modules.knowledge.documenttype.application.action.CreateWorkspaceDocumentTypeAction;
import com.company.scopery.modules.knowledge.documenttype.application.action.DeactivateDocumentTypeAction;
import com.company.scopery.modules.knowledge.documenttype.application.action.SoftDeleteDocumentTypeAction;
import com.company.scopery.modules.knowledge.documenttype.application.action.UpdateDocumentTypeAction;
import com.company.scopery.modules.knowledge.documenttype.application.command.CreateDocumentTypeCommand;
import com.company.scopery.modules.knowledge.documenttype.application.command.UpdateDocumentTypeCommand;
import com.company.scopery.modules.knowledge.documenttype.application.response.DocumentTypeResponse;
import com.company.scopery.modules.knowledge.documenttype.application.service.DocumentTypeQueryService;
import com.company.scopery.modules.knowledge.documenttype.application.support.DocumentTypePlatformPublisher;
import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentClassification;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentTypeRepository;
import com.company.scopery.modules.knowledge.documenttype.domain.valueobject.DocumentTypeCode;
import com.company.scopery.modules.knowledge.shared.activity.KnowledgeActivityLogger;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeErrorCatalog;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceJoinPolicy;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceVisibility;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.valueobject.WorkspaceCode;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentTypeActionTest {

    @Mock private DocumentTypeRepository documentTypeRepository;
    @Mock private WorkspaceRepository workspaceRepository;
    @Mock private KnowledgeActivityLogger activityLogger;
    @Mock private CurrentUserAuthorizationService currentUserService;
    @Mock private WorkspaceIamIntegrationService workspaceIamIntegrationService;
    @Mock private IamSystemAuthorizationService systemAuthorizationService;
    @Mock private DocumentTypePlatformPublisher platformPublisher;

    private IamUser currentUser;

    private ActivateDocumentTypeAction activateDocumentTypeAction;
    private CreateDocumentTypeAction createDocumentTypeAction;
    private CreateSystemDocumentTypeAction createSystemDocumentTypeAction;
    private CreateWorkspaceDocumentTypeAction createWorkspaceDocumentTypeAction;
    private DeactivateDocumentTypeAction deactivateDocumentTypeAction;
    private DocumentTypeQueryService documentTypeQueryService;
    private SoftDeleteDocumentTypeAction softDeleteDocumentTypeAction;
    private ArchiveDocumentTypeAction archiveDocumentTypeAction;
    private UpdateDocumentTypeAction updateDocumentTypeAction;

    @BeforeEach
    void setUp() {
        createDocumentTypeAction = new CreateDocumentTypeAction(
                documentTypeRepository, workspaceRepository, currentUserService,
                workspaceIamIntegrationService, systemAuthorizationService, activityLogger, platformPublisher);
        activateDocumentTypeAction = new ActivateDocumentTypeAction(documentTypeRepository,
                currentUserService, workspaceIamIntegrationService, systemAuthorizationService,
                activityLogger, platformPublisher);
        createSystemDocumentTypeAction = new CreateSystemDocumentTypeAction(
                createDocumentTypeAction, systemAuthorizationService);
        createWorkspaceDocumentTypeAction = new CreateWorkspaceDocumentTypeAction(createDocumentTypeAction);
        deactivateDocumentTypeAction = new DeactivateDocumentTypeAction(documentTypeRepository,
                currentUserService, workspaceIamIntegrationService, systemAuthorizationService,
                activityLogger, platformPublisher);
        documentTypeQueryService = new DocumentTypeQueryService(documentTypeRepository,
                currentUserService, workspaceIamIntegrationService);
        archiveDocumentTypeAction = new ArchiveDocumentTypeAction(documentTypeRepository, activityLogger,
                currentUserService, workspaceIamIntegrationService, systemAuthorizationService, platformPublisher);
        softDeleteDocumentTypeAction = new SoftDeleteDocumentTypeAction(archiveDocumentTypeAction);
        updateDocumentTypeAction = new UpdateDocumentTypeAction(documentTypeRepository,
                currentUserService, workspaceIamIntegrationService, systemAuthorizationService,
                activityLogger, platformPublisher);
        Instant now = Instant.now();
        currentUser = IamUser.of(UUID.randomUUID(), Username.of("admin"),
                EmailAddress.of("admin@example.com"), "Admin", null, IamUserStatus.ACTIVE, now, now);
        lenient().when(currentUserService.resolveCurrentUser()).thenReturn(currentUser);
        lenient().when(platformPublisher.isValidJson(any())).thenReturn(true);
    }

    @Test
    void createSystemDocumentType_success_returnsActiveSystemType() {
        when(documentTypeRepository.existsByCodeAndScopeSystem(DocumentTypeCode.of("ARTICLE"))).thenReturn(false);
        when(documentTypeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DocumentTypeResponse response = createSystemDocumentTypeAction.execute(
                new CreateDocumentTypeCommand("ARTICLE", "Article", "General article", "SYSTEM", null));

        assertThat(response.code()).isEqualTo("ARTICLE");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.documentScope()).isEqualTo("SYSTEM");
        assertThat(response.isSystem()).isTrue();
        assertThat(response.workspaceId()).isNull();
        assertThat(response.defaultClassification()).isEqualTo("INTERNAL");
        verify(platformPublisher).enqueue(any(), eq("DOCUMENT_TYPE_CREATED"));
    }

    @Test
    void createSystemDocumentType_withWorkspaceId_throwsBadRequest() {
        assertThatThrownBy(() -> createSystemDocumentTypeAction.execute(
                new CreateDocumentTypeCommand("ARTICLE", "Article", null, "SYSTEM", UUID.randomUUID())))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            KnowledgeErrorCatalog.DOCUMENT_TYPE_SYSTEM_SCOPE_MUST_NOT_HAVE_WORKSPACE_ID.code());
                });
    }

    @Test
    void createSystemDocumentType_duplicateCode_throws409() {
        when(documentTypeRepository.existsByCodeAndScopeSystem(DocumentTypeCode.of("ARTICLE"))).thenReturn(true);

        assertThatThrownBy(() -> createSystemDocumentTypeAction.execute(
                new CreateDocumentTypeCommand("ARTICLE", "Article", null, "SYSTEM", null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            KnowledgeErrorCatalog.DOCUMENT_TYPE_CODE_ALREADY_EXISTS.code());
                });
    }

    @Test
    void createUnified_organizationScope_success() {
        UUID orgId = UUID.randomUUID();
        when(documentTypeRepository.existsByCodeAndOrganizationId(DocumentTypeCode.of("ORG_POLICY"), orgId))
                .thenReturn(false);
        when(documentTypeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DocumentTypeResponse response = createDocumentTypeAction.execute(new CreateDocumentTypeCommand(
                "ORG_POLICY", "Org Policy", null, "ORGANIZATION", orgId, null,
                "COMPLIANCE", "CONFIDENTIAL", 90, null, null));

        assertThat(response.documentScope()).isEqualTo("ORGANIZATION");
        assertThat(response.organizationId()).isEqualTo(orgId);
        assertThat(response.defaultClassification()).isEqualTo("CONFIDENTIAL");
    }

    @Test
    void createWorkspaceDocumentType_success_returnsWorkspaceType() {
        UUID workspaceId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(
                Workspace.create(orgId, "WS", WorkspaceCode.of("WS_A"), null, currentUser.id(),
                        WorkspaceVisibility.PRIVATE, WorkspaceJoinPolicy.INVITE_ONLY)));
        when(documentTypeRepository.existsByCodeAndWorkspaceId(
                DocumentTypeCode.of("SPRINT_DOC"), workspaceId)).thenReturn(false);
        when(documentTypeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DocumentTypeResponse response = createWorkspaceDocumentTypeAction.execute(
                new CreateDocumentTypeCommand("SPRINT_DOC", "Sprint Doc", null, "WORKSPACE", workspaceId));

        assertThat(response.code()).isEqualTo("SPRINT_DOC");
        assertThat(response.documentScope()).isEqualTo("WORKSPACE");
        assertThat(response.workspaceId()).isEqualTo(workspaceId);
        assertThat(response.organizationId()).isEqualTo(orgId);
        assertThat(response.isSystem()).isFalse();
    }

    @Test
    void createWorkspaceDocumentType_missingWorkspaceId_throwsBadRequest() {
        assertThatThrownBy(() -> createWorkspaceDocumentTypeAction.execute(
                new CreateDocumentTypeCommand("SPRINT_DOC", "Sprint Doc", null, "WORKSPACE", null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            KnowledgeErrorCatalog.DOCUMENT_TYPE_WORKSPACE_SCOPE_REQUIRES_WORKSPACE_ID.code());
                });
    }

    @Test
    void createWorkspaceDocumentType_duplicateCodeInWorkspace_throws409() {
        UUID workspaceId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(
                Workspace.create(orgId, "WS", WorkspaceCode.of("WS_B"), null, currentUser.id(),
                        WorkspaceVisibility.PRIVATE, WorkspaceJoinPolicy.INVITE_ONLY)));
        when(documentTypeRepository.existsByCodeAndWorkspaceId(
                DocumentTypeCode.of("SPRINT_DOC"), workspaceId)).thenReturn(true);

        assertThatThrownBy(() -> createWorkspaceDocumentTypeAction.execute(
                new CreateDocumentTypeCommand("SPRINT_DOC", "Sprint Doc", null, "WORKSPACE", workspaceId)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(KnowledgeErrorCatalog.DOCUMENT_TYPE_WORKSPACE_CODE_ALREADY_EXISTS.code()));
    }

    @Test
    void updateDocumentType_success_returnsUpdated() {
        DocumentType existing = systemDocumentType("ARTICLE");
        when(documentTypeRepository.findById(existing.id())).thenReturn(Optional.of(existing));
        when(documentTypeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DocumentTypeResponse response = updateDocumentTypeAction.execute(
                new UpdateDocumentTypeCommand(existing.id(), "Updated Article", "New description"));

        assertThat(response.name()).isEqualTo("Updated Article");
        verify(platformPublisher).enqueue(any(), eq("DOCUMENT_TYPE_UPDATED"));
    }

    @Test
    void updateDocumentType_archived_throws422() {
        DocumentType existing = systemDocumentType("ARTICLE");
        DocumentType archived = existing.archive(UUID.randomUUID());
        when(documentTypeRepository.findById(existing.id())).thenReturn(Optional.of(archived));

        assertThatThrownBy(() -> updateDocumentTypeAction.execute(
                new UpdateDocumentTypeCommand(existing.id(), "Updated", null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(KnowledgeErrorCatalog.DOCUMENT_TYPE_ARCHIVED.code()));
    }

    @Test
    void archiveDocumentType_builtIn_throws422() {
        DocumentType builtIn = DocumentType.createSystem(
                DocumentTypeCode.of("BRD"), "BRD", "desc", null, DocumentClassification.INTERNAL,
                null, null, null, true);
        when(documentTypeRepository.findById(builtIn.id())).thenReturn(Optional.of(builtIn));

        assertThatThrownBy(() -> archiveDocumentTypeAction.execute(builtIn.id()))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            KnowledgeErrorCatalog.DOCUMENT_TYPE_BUILT_IN_CANNOT_DELETE.code());
                });
    }

    @Test
    void softDeleteDocumentType_workspaceType_archives() {
        UUID workspaceId = UUID.randomUUID();
        DocumentType wsType = workspaceDocumentType("SPRINT_DOC", workspaceId);
        when(documentTypeRepository.findById(wsType.id())).thenReturn(Optional.of(wsType));
        when(documentTypeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DocumentTypeResponse response = softDeleteDocumentTypeAction.execute(wsType.id());

        assertThat(response.status()).isEqualTo("ARCHIVED");
        assertThat(response.archivedAt()).isNotNull();
        verify(platformPublisher).enqueue(any(), eq("DOCUMENT_TYPE_ARCHIVED"));
    }

    @Test
    void deactivateDocumentType_active_returnsInactive() {
        DocumentType existing = systemDocumentType("ARTICLE");
        when(documentTypeRepository.findById(existing.id())).thenReturn(Optional.of(existing));
        when(documentTypeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DocumentTypeResponse response = deactivateDocumentTypeAction.execute(existing.id());

        assertThat(response.status()).isEqualTo("INACTIVE");
        verify(platformPublisher).enqueue(any(), eq("DOCUMENT_TYPE_DEACTIVATED"));
    }

    @Test
    void activateDocumentType_inactive_returnsActive() {
        DocumentType inactive = systemDocumentType("ARTICLE").deactivate();
        when(documentTypeRepository.findById(inactive.id())).thenReturn(Optional.of(inactive));
        when(documentTypeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DocumentTypeResponse response = activateDocumentTypeAction.execute(inactive.id());

        assertThat(response.status()).isEqualTo("ACTIVE");
        verify(platformPublisher).enqueue(any(), eq("DOCUMENT_TYPE_ACTIVATED"));
    }

    @Test
    void getDocumentType_notFound_throws404() {
        UUID id = UUID.randomUUID();
        when(documentTypeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentTypeQueryService.getDocumentType(id))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void createWithInvalidClassification_throws400() {
        assertThatThrownBy(() -> createDocumentTypeAction.execute(new CreateDocumentTypeCommand(
                "X", "X", null, "SYSTEM", null, null, null, "NOT_A_CLASS", null, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(KnowledgeErrorCatalog.DOCUMENT_TYPE_INVALID_CLASSIFICATION.code()));
    }

    private DocumentType systemDocumentType(String code) {
        return DocumentType.createSystem(DocumentTypeCode.of(code), code + " name", "description");
    }

    private DocumentType workspaceDocumentType(String code, UUID workspaceId) {
        return DocumentType.createWorkspace(DocumentTypeCode.of(code), code + " name", "description",
                UUID.randomUUID(), workspaceId, null, DocumentClassification.INTERNAL, null, null, null);
    }
}
