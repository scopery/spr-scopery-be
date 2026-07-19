package com.company.scopery.modules.knowledge.documenttypefield.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentClassification;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;
import com.company.scopery.modules.knowledge.documenttype.domain.valueobject.DocumentTypeCode;
import com.company.scopery.modules.knowledge.documenttypefield.application.action.ArchiveDocumentTypeFieldAction;
import com.company.scopery.modules.knowledge.documenttypefield.application.action.CreateDocumentTypeFieldAction;
import com.company.scopery.modules.knowledge.documenttypefield.application.action.ReorderDocumentTypeFieldsAction;
import com.company.scopery.modules.knowledge.documenttypefield.application.action.UpdateDocumentTypeFieldAction;
import com.company.scopery.modules.knowledge.documenttypefield.application.command.CreateDocumentTypeFieldCommand;
import com.company.scopery.modules.knowledge.documenttypefield.application.command.ReorderDocumentTypeFieldsCommand;
import com.company.scopery.modules.knowledge.documenttypefield.application.command.UpdateDocumentTypeFieldCommand;
import com.company.scopery.modules.knowledge.documenttypefield.application.response.DocumentTypeFieldResponse;
import com.company.scopery.modules.knowledge.documenttypefield.application.support.DocumentTypeFieldAccessGuard;
import com.company.scopery.modules.knowledge.documenttypefield.application.support.DocumentTypeFieldPlatformPublisher;
import com.company.scopery.modules.knowledge.documenttypefield.domain.enums.DocumentTypeFieldDataType;
import com.company.scopery.modules.knowledge.documenttypefield.domain.model.DocumentTypeField;
import com.company.scopery.modules.knowledge.documenttypefield.domain.model.DocumentTypeFieldRepository;
import com.company.scopery.modules.knowledge.documenttypefield.domain.valueobject.DocumentTypeFieldKey;
import com.company.scopery.modules.knowledge.shared.activity.KnowledgeActivityLogger;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeErrorCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentTypeFieldActionTest {

    @Mock private DocumentTypeFieldRepository fieldRepository;
    @Mock private DocumentTypeFieldAccessGuard accessGuard;
    @Mock private KnowledgeActivityLogger activityLogger;
    @Mock private DocumentTypeFieldPlatformPublisher platformPublisher;
    @Mock private CurrentUserAuthorizationService currentUserService;

    private CreateDocumentTypeFieldAction createAction;
    private UpdateDocumentTypeFieldAction updateAction;
    private ArchiveDocumentTypeFieldAction archiveAction;
    private ReorderDocumentTypeFieldsAction reorderAction;

    private DocumentType documentType;
    private IamUser currentUser;

    @BeforeEach
    void setUp() {
        createAction = new CreateDocumentTypeFieldAction(
                fieldRepository, accessGuard, activityLogger, platformPublisher);
        updateAction = new UpdateDocumentTypeFieldAction(
                fieldRepository, accessGuard, activityLogger, platformPublisher, currentUserService);
        archiveAction = new ArchiveDocumentTypeFieldAction(
                fieldRepository, accessGuard, activityLogger, platformPublisher);
        reorderAction = new ReorderDocumentTypeFieldsAction(
                fieldRepository, accessGuard, activityLogger, platformPublisher);

        documentType = DocumentType.createSystem(DocumentTypeCode.of("BRD"), "BRD", "desc");
        Instant now = Instant.now();
        currentUser = IamUser.of(UUID.randomUUID(), Username.of("admin"),
                EmailAddress.of("admin@example.com"), "Admin", null, IamUserStatus.ACTIVE, now, now);

        lenient().when(accessGuard.requireDocumentType(documentType.id())).thenReturn(documentType);
        lenient().when(platformPublisher.isValidJson(any())).thenReturn(true);
        lenient().when(currentUserService.resolveCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void createField_success() {
        when(fieldRepository.existsByDocumentTypeIdAndFieldKey(documentType.id(), "summary")).thenReturn(false);
        when(fieldRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DocumentTypeFieldResponse response = createAction.execute(new CreateDocumentTypeFieldCommand(
                documentType.id(), "summary", "Summary", null, "LONG_TEXT",
                false, false, null, null, null, 1));

        assertThat(response.fieldKey()).isEqualTo("summary");
        assertThat(response.dataType()).isEqualTo("LONG_TEXT");
        verify(platformPublisher).enqueue(any(), eq("DOCUMENT_TYPE_FIELD_CREATED"));
    }

    @Test
    void createSelectWithoutOptions_throws400() {
        when(fieldRepository.existsByDocumentTypeIdAndFieldKey(documentType.id(), "priority")).thenReturn(false);

        assertThatThrownBy(() -> createAction.execute(new CreateDocumentTypeFieldCommand(
                documentType.id(), "priority", "Priority", null, "SELECT",
                false, false, null, null, null, 0)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(KnowledgeErrorCatalog.DOCUMENT_TYPE_FIELD_OPTIONS_REQUIRED.code()));
    }

    @Test
    void createDuplicateKey_throws409() {
        when(fieldRepository.existsByDocumentTypeIdAndFieldKey(documentType.id(), "summary")).thenReturn(true);

        assertThatThrownBy(() -> createAction.execute(new CreateDocumentTypeFieldCommand(
                documentType.id(), "summary", "Summary", null, "TEXT",
                false, false, null, null, null, 0)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(KnowledgeErrorCatalog.DOCUMENT_TYPE_FIELD_CODE_ALREADY_EXISTS.code()));
    }

    @Test
    void archiveSystemField_throws422() {
        DocumentTypeField systemField = DocumentTypeField.create(
                documentType.id(), DocumentTypeFieldKey.of("owner"), "Owner", null,
                DocumentTypeFieldDataType.USER, true, true, null, null, null, 0);
        when(fieldRepository.findById(systemField.id())).thenReturn(Optional.of(systemField));

        assertThatThrownBy(() -> archiveAction.execute(documentType.id(), systemField.id()))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(KnowledgeErrorCatalog.DOCUMENT_TYPE_FIELD_SYSTEM_CANNOT_ARCHIVE.code()));
    }

    @Test
    void updateField_pathMismatch_throws422() {
        DocumentTypeField field = DocumentTypeField.create(
                UUID.randomUUID(), DocumentTypeFieldKey.of("summary"), "Summary", null,
                DocumentTypeFieldDataType.TEXT, false, false, null, null, null, 0);
        when(fieldRepository.findById(field.id())).thenReturn(Optional.of(field));

        assertThatThrownBy(() -> updateAction.execute(new UpdateDocumentTypeFieldCommand(
                documentType.id(), field.id(), "Summary", null, "TEXT",
                false, null, null, null, 0)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(KnowledgeErrorCatalog.DOCUMENT_TYPE_FIELD_PATH_MISMATCH.code()));
    }

    @Test
    void reorderFields_success() {
        DocumentTypeField a = DocumentTypeField.create(
                documentType.id(), DocumentTypeFieldKey.of("a"), "A", null,
                DocumentTypeFieldDataType.TEXT, false, false, null, null, null, 0);
        DocumentTypeField b = DocumentTypeField.create(
                documentType.id(), DocumentTypeFieldKey.of("b"), "B", null,
                DocumentTypeFieldDataType.TEXT, false, false, null, null, null, 1);
        when(fieldRepository.findByDocumentTypeId(documentType.id())).thenReturn(List.of(a, b));

        List<DocumentTypeFieldResponse> result = reorderAction.execute(
                new ReorderDocumentTypeFieldsCommand(documentType.id(), List.of(b.id(), a.id())));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).fieldKey()).isEqualTo("b");
        assertThat(result.get(0).displayOrder()).isEqualTo(0);
        assertThat(result.get(1).fieldKey()).isEqualTo("a");
        assertThat(result.get(1).displayOrder()).isEqualTo(1);
        verify(fieldRepository).saveAll(any());
    }

    @Test
    void reorderFields_incompleteSet_throws400() {
        DocumentTypeField a = DocumentTypeField.create(
                documentType.id(), DocumentTypeFieldKey.of("a"), "A", null,
                DocumentTypeFieldDataType.TEXT, false, false, null, null, null, 0);
        when(fieldRepository.findByDocumentTypeId(documentType.id())).thenReturn(List.of(a));

        assertThatThrownBy(() -> reorderAction.execute(
                new ReorderDocumentTypeFieldsCommand(documentType.id(), List.of(UUID.randomUUID()))))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(KnowledgeErrorCatalog.DOCUMENT_TYPE_FIELD_REORDER_INVALID.code()));
    }
}
