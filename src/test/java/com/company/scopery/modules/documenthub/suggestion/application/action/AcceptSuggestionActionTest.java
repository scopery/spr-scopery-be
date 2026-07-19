package com.company.scopery.modules.documenthub.suggestion.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.documenthub.document.domain.enums.ContentMode;
import com.company.scopery.modules.documenthub.document.domain.model.Document;
import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.nativecontent.application.action.SaveDocumentContentAction;
import com.company.scopery.modules.documenthub.nativecontent.application.response.DocumentContentResponse;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubErrorCatalog;
import com.company.scopery.modules.documenthub.suggestion.application.command.AcceptSuggestionCommand;
import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestion;
import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestionOperation;
import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestionOperationRepository;
import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests BR-NDE-032 (accept is atomic) and BR-NDE-033 (stale suggestion not applied).
 * Also tests BR-NDE-037 (accept goes through single write path SaveDocumentContentAction).
 */
@ExtendWith(MockitoExtension.class)
class AcceptSuggestionActionTest {

    @Mock DocumentRepository documents;
    @Mock DocumentSuggestionRepository suggestionRepo;
    @Mock DocumentSuggestionOperationRepository operationRepo;
    @Mock SaveDocumentContentAction saveContent;
    @Mock DocumentHubAuthorizationService authorization;
    @Mock DocumentHubActivityLogger activityLogger;
    @Mock TransactionalOutboxService outbox;

    AcceptSuggestionAction action;

    final UUID projectId = UUID.randomUUID();
    final UUID documentId = UUID.randomUUID();
    final UUID suggestionId = UUID.randomUUID();
    final UUID workspaceId = UUID.randomUUID();
    final String newAst = "{\"type\":\"doc\",\"content\":[{\"type\":\"paragraph\",\"text\":\"accepted\"}]}";

    @BeforeEach
    void setUp() {
        action = new AcceptSuggestionAction(documents, suggestionRepo, operationRepo,
                saveContent, authorization, activityLogger, outbox);
    }

    @Test
    void execute_acceptsSuccessfully_whenRevisionMatches() {
        // BR-NDE-032: atomic accept — suggestion and revision update in same transaction
        Document doc = nativeDocumentAtRevision(3L);
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));

        DocumentSuggestion pending = pendingSuggestion(3L);
        when(suggestionRepo.findByIdAndDocumentId(suggestionId, documentId)).thenReturn(Optional.of(pending));

        DocumentSuggestionOperation op = operation(suggestionId, newAst);
        when(operationRepo.findBySuggestionId(suggestionId)).thenReturn(List.of(op));

        DocumentContentResponse savedContent = stubContentResponse(4L);
        when(saveContent.execute(any())).thenReturn(savedContent);

        DocumentSuggestion accepted = pending.accept(UUID.randomUUID(), 4L);
        when(suggestionRepo.save(any())).thenReturn(accepted);

        var response = action.execute(new AcceptSuggestionCommand(projectId, documentId, suggestionId));

        assertThat(response.status()).isEqualTo("ACCEPTED");
        assertThat(response.acceptedRevisionNo()).isEqualTo(4L);
        verify(saveContent).execute(any());
        verify(suggestionRepo).save(any());
        verify(outbox).enqueue(any(), eq(accepted.id()), any(), any());
    }

    @Test
    void execute_throwsConflict_whenSuggestionTargetRevisionIsStale() {
        // BR-NDE-033: stale suggestion must not auto-merge
        Document doc = nativeDocumentAtRevision(5L); // current = 5
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));

        DocumentSuggestion stale = pendingSuggestion(3L); // proposed against revision 3
        when(suggestionRepo.findByIdAndDocumentId(suggestionId, documentId)).thenReturn(Optional.of(stale));

        assertThatThrownBy(() -> action.execute(new AcceptSuggestionCommand(projectId, documentId, suggestionId)))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.SUGGESTION_BASE_REVISION_CONFLICT.code()));

        verify(saveContent, never()).execute(any());
        verify(suggestionRepo, never()).save(any());
    }

    @Test
    void execute_throwsIllegalState_whenNoOperationHasValue() {
        Document doc = nativeDocumentAtRevision(1L);
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));

        DocumentSuggestion pending = pendingSuggestion(1L);
        when(suggestionRepo.findByIdAndDocumentId(suggestionId, documentId)).thenReturn(Optional.of(pending));

        // Operation with null value — no applicable AST
        DocumentSuggestionOperation noValueOp = operationWithNullValue(suggestionId);
        when(operationRepo.findBySuggestionId(suggestionId)).thenReturn(List.of(noValueOp));

        assertThatThrownBy(() -> action.execute(new AcceptSuggestionCommand(projectId, documentId, suggestionId)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no applicable AST operation");
    }

    @Test
    void execute_throwsIllegalState_whenOperationsEmpty() {
        Document doc = nativeDocumentAtRevision(1L);
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));

        DocumentSuggestion pending = pendingSuggestion(1L);
        when(suggestionRepo.findByIdAndDocumentId(suggestionId, documentId)).thenReturn(Optional.of(pending));
        when(operationRepo.findBySuggestionId(suggestionId)).thenReturn(List.of());

        assertThatThrownBy(() -> action.execute(new AcceptSuggestionCommand(projectId, documentId, suggestionId)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void execute_throwsNotFound_whenSuggestionMissing() {
        Document doc = nativeDocumentAtRevision(1L);
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));
        when(suggestionRepo.findByIdAndDocumentId(suggestionId, documentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> action.execute(new AcceptSuggestionCommand(projectId, documentId, suggestionId)))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.SUGGESTION_NOT_FOUND.code()));
    }

    @Test
    void execute_throwsNotFound_whenDocumentMissing() {
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> action.execute(new AcceptSuggestionCommand(projectId, documentId, suggestionId)))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.DOCUMENT_NOT_FOUND.code()));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Document nativeDocumentAtRevision(long revisionNo) {
        return new Document(documentId, workspaceId, projectId, null, "SPEC", "D-1", "Title", null,
                com.company.scopery.modules.documenthub.document.domain.enums.DocumentStatus.DRAFT,
                "INTERNAL", null, false, null, null, null, null, null, 0, Instant.now(), Instant.now(),
                ContentMode.NATIVE, null, UUID.randomUUID(), revisionNo, 1, "chk",
                Instant.now(), null, null, null, null,
                com.company.scopery.modules.documenthub.document.domain.enums.ContentWidth.CENTERED, false);
    }

    private DocumentSuggestion pendingSuggestion(long targetRevisionNo) {
        return new DocumentSuggestion(suggestionId, documentId, workspaceId, projectId,
                targetRevisionNo, "desc",
                com.company.scopery.modules.documenthub.suggestion.domain.enums.SuggestionStatus.PENDING,
                null, null, null, null, null, Instant.now(), Instant.now());
    }

    private DocumentSuggestionOperation operation(UUID sid, String value) {
        return new DocumentSuggestionOperation(UUID.randomUUID(), sid, "REPLACE", "block-1", null,
                value, 0, Instant.now(), Instant.now());
    }

    private DocumentSuggestionOperation operationWithNullValue(UUID sid) {
        return new DocumentSuggestionOperation(UUID.randomUUID(), sid, "REPLACE", "block-1", null,
                null, 0, Instant.now(), Instant.now());
    }

    private DocumentContentResponse stubContentResponse(long revisionNo) {
        return new DocumentContentResponse(UUID.randomUUID(), documentId, revisionNo,
                newAst, "checksum", 1, 1, 8, Instant.now(), null, Instant.now(), Instant.now());
    }
}
