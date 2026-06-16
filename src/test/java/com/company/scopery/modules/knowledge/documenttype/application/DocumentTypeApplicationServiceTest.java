package com.company.scopery.modules.knowledge.documenttype.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.EmailAddress;
import com.company.scopery.modules.iam.user.domain.IamUser;
import com.company.scopery.modules.iam.user.domain.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.Username;
import com.company.scopery.modules.knowledge.documenttype.application.command.CreateDocumentTypeCommand;
import com.company.scopery.modules.knowledge.documenttype.application.command.UpdateDocumentTypeCommand;
import com.company.scopery.modules.knowledge.documenttype.application.response.DocumentTypeResponse;
import com.company.scopery.modules.knowledge.documenttype.domain.DocumentType;
import com.company.scopery.modules.knowledge.documenttype.domain.DocumentTypeCode;
import com.company.scopery.modules.knowledge.documenttype.domain.DocumentTypeRepository;
import com.company.scopery.modules.knowledge.shared.activity.KnowledgeActivityLogger;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeErrorCatalog;
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
class DocumentTypeApplicationServiceTest {

    @Mock private DocumentTypeRepository documentTypeRepository;
    @Mock private KnowledgeActivityLogger activityLogger;
    @Mock private CurrentUserAuthorizationService currentUserService;

    private DocumentTypeApplicationService service;
    private IamUser currentUser;

    @BeforeEach
    void setUp() {
        service = new DocumentTypeApplicationService(documentTypeRepository, activityLogger, currentUserService);
        Instant now = Instant.now();
        currentUser = new IamUser(UUID.randomUUID(), Username.of("admin"),
                EmailAddress.of("admin@example.com"), "Admin", null, IamUserStatus.ACTIVE, now, now);
        lenient().when(currentUserService.resolveCurrentUser()).thenReturn(currentUser);
    }

    // ── createSystemDocumentType ─────────────────────────────────────────────

    @Test
    void createSystemDocumentType_success_returnsActiveSystemType() {
        when(documentTypeRepository.existsByCodeAndScopeSystem(DocumentTypeCode.of("ARTICLE"))).thenReturn(false);
        when(documentTypeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DocumentTypeResponse response = service.createSystemDocumentType(
                new CreateDocumentTypeCommand("ARTICLE", "Article", "General article", "SYSTEM", null));

        assertThat(response.code()).isEqualTo("ARTICLE");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.documentScope()).isEqualTo("SYSTEM");
        assertThat(response.isSystem()).isTrue();
        assertThat(response.workspaceId()).isNull();
    }

    @Test
    void createSystemDocumentType_withWorkspaceId_throwsBadRequest() {
        assertThatThrownBy(() -> service.createSystemDocumentType(
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

        assertThatThrownBy(() -> service.createSystemDocumentType(
                new CreateDocumentTypeCommand("ARTICLE", "Article", null, "SYSTEM", null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            KnowledgeErrorCatalog.DOCUMENT_TYPE_CODE_ALREADY_EXISTS.code());
                });
    }

    // ── createWorkspaceDocumentType ──────────────────────────────────────────

    @Test
    void createWorkspaceDocumentType_success_returnsWorkspaceType() {
        UUID workspaceId = UUID.randomUUID();
        when(documentTypeRepository.existsByCodeAndWorkspaceId(
                DocumentTypeCode.of("SPRINT_DOC"), workspaceId)).thenReturn(false);
        when(documentTypeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DocumentTypeResponse response = service.createWorkspaceDocumentType(
                new CreateDocumentTypeCommand("SPRINT_DOC", "Sprint Doc", null, "WORKSPACE", workspaceId));

        assertThat(response.code()).isEqualTo("SPRINT_DOC");
        assertThat(response.documentScope()).isEqualTo("WORKSPACE");
        assertThat(response.workspaceId()).isEqualTo(workspaceId);
        assertThat(response.isSystem()).isFalse();
    }

    @Test
    void createWorkspaceDocumentType_missingWorkspaceId_throwsBadRequest() {
        assertThatThrownBy(() -> service.createWorkspaceDocumentType(
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
        when(documentTypeRepository.existsByCodeAndWorkspaceId(
                DocumentTypeCode.of("SPRINT_DOC"), workspaceId)).thenReturn(true);

        assertThatThrownBy(() -> service.createWorkspaceDocumentType(
                new CreateDocumentTypeCommand("SPRINT_DOC", "Sprint Doc", null, "WORKSPACE", workspaceId)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(KnowledgeErrorCatalog.DOCUMENT_TYPE_WORKSPACE_CODE_ALREADY_EXISTS.code()));
    }

    // ── updateDocumentType ───────────────────────────────────────────────────

    @Test
    void updateDocumentType_success_returnsUpdated() {
        DocumentType existing = systemDocumentType("ARTICLE");
        when(documentTypeRepository.findById(existing.id())).thenReturn(Optional.of(existing));
        when(documentTypeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DocumentTypeResponse response = service.updateDocumentType(
                new UpdateDocumentTypeCommand(existing.id(), "Updated Article", "New description"));

        assertThat(response.name()).isEqualTo("Updated Article");
    }

    @Test
    void updateDocumentType_deleted_throws422() {
        DocumentType existing = systemDocumentType("ARTICLE");
        DocumentType deleted = existing.softDelete(UUID.randomUUID());
        when(documentTypeRepository.findById(existing.id())).thenReturn(Optional.of(deleted));

        assertThatThrownBy(() -> service.updateDocumentType(
                new UpdateDocumentTypeCommand(existing.id(), "Updated", null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(KnowledgeErrorCatalog.DOCUMENT_TYPE_DELETED_CANNOT_BE_MODIFIED.code()));
    }

    // ── softDeleteDocumentType ───────────────────────────────────────────────

    @Test
    void softDeleteDocumentType_systemType_throws422() {
        DocumentType systemType = systemDocumentType("ARTICLE");
        when(documentTypeRepository.findById(systemType.id())).thenReturn(Optional.of(systemType));

        assertThatThrownBy(() -> service.softDeleteDocumentType(systemType.id()))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            KnowledgeErrorCatalog.DOCUMENT_TYPE_SYSTEM_CANNOT_BE_DELETED.code());
                });
    }

    @Test
    void softDeleteDocumentType_workspaceType_succeeds() {
        UUID workspaceId = UUID.randomUUID();
        DocumentType wsType = workspaceDocumentType("SPRINT_DOC", workspaceId);
        when(documentTypeRepository.findById(wsType.id())).thenReturn(Optional.of(wsType));
        when(documentTypeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DocumentTypeResponse response = service.softDeleteDocumentType(wsType.id());

        assertThat(response.status()).isEqualTo("DELETED");
        assertThat(response.deletedAt()).isNotNull();
    }

    // ── activate/deactivate ──────────────────────────────────────────────────

    @Test
    void deactivateDocumentType_active_returnsInactive() {
        DocumentType existing = systemDocumentType("ARTICLE");
        when(documentTypeRepository.findById(existing.id())).thenReturn(Optional.of(existing));
        when(documentTypeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DocumentTypeResponse response = service.deactivateDocumentType(existing.id());

        assertThat(response.status()).isEqualTo("INACTIVE");
    }

    @Test
    void activateDocumentType_inactive_returnsActive() {
        DocumentType inactive = systemDocumentType("ARTICLE").deactivate();
        when(documentTypeRepository.findById(inactive.id())).thenReturn(Optional.of(inactive));
        when(documentTypeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DocumentTypeResponse response = service.activateDocumentType(inactive.id());

        assertThat(response.status()).isEqualTo("ACTIVE");
    }

    @Test
    void getDocumentType_notFound_throws404() {
        UUID id = UUID.randomUUID();
        when(documentTypeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getDocumentType(id))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private DocumentType systemDocumentType(String code) {
        return DocumentType.createSystem(DocumentTypeCode.of(code), code + " name", "description");
    }

    private DocumentType workspaceDocumentType(String code, UUID workspaceId) {
        return DocumentType.createWorkspace(DocumentTypeCode.of(code), code + " name", "description", workspaceId);
    }
}
