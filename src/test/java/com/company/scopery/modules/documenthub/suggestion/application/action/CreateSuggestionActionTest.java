package com.company.scopery.modules.documenthub.suggestion.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.documenthub.document.domain.enums.ContentMode;
import com.company.scopery.modules.documenthub.document.domain.model.Document;
import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubErrorCatalog;
import com.company.scopery.modules.documenthub.suggestion.application.command.CreateSuggestionCommand;
import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestion;
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
 * Tests BR-NDE-033: suggestion must be proposed against exact current revision.
 */
@ExtendWith(MockitoExtension.class)
class CreateSuggestionActionTest {

    @Mock DocumentRepository documents;
    @Mock DocumentSuggestionRepository suggestionRepo;
    @Mock DocumentSuggestionOperationRepository operationRepo;
    @Mock DocumentHubAuthorizationService authorization;
    @Mock DocumentHubActivityLogger activityLogger;
    @Mock TransactionalOutboxService outbox;

    CreateSuggestionAction action;

    final UUID projectId = UUID.randomUUID();
    final UUID documentId = UUID.randomUUID();
    final UUID workspaceId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        action = new CreateSuggestionAction(documents, suggestionRepo, operationRepo,
                authorization, activityLogger, outbox);
    }

    @Test
    void execute_createsSuccessfully_whenRevisionMatches() {
        Document doc = nativeDocumentAtRevision(3L);
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));
        DocumentSuggestion saved = DocumentSuggestion.create(documentId, workspaceId, projectId, 3L, "Fix typo");
        when(suggestionRepo.save(any())).thenReturn(saved);

        var cmd = new CreateSuggestionCommand(projectId, documentId, 3L, "Fix typo", List.of());
        var response = action.execute(cmd);

        assertThat(response.targetRevisionNo()).isEqualTo(3L);
        verify(suggestionRepo).save(any());
        verify(outbox).enqueue(any(), any(), any(), any());
    }

    @Test
    void execute_throwsConflict_whenTargetRevisionIsStale() {
        // BR-NDE-033: proposing suggestion against stale revision
        Document doc = nativeDocumentAtRevision(5L);
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));

        // Proposing against revision 3 but document is now at revision 5
        var cmd = new CreateSuggestionCommand(projectId, documentId, 3L, "Fix typo", List.of());

        assertThatThrownBy(() -> action.execute(cmd))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.SUGGESTION_BASE_REVISION_CONFLICT.code()));

        verify(suggestionRepo, never()).save(any());
    }

    @Test
    void execute_throwsConflict_whenTargetRevisionIsAhead() {
        // Proposing against future revision — also a conflict
        Document doc = nativeDocumentAtRevision(2L);
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));

        var cmd = new CreateSuggestionCommand(projectId, documentId, 5L, "Add section", List.of());

        assertThatThrownBy(() -> action.execute(cmd))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.SUGGESTION_BASE_REVISION_CONFLICT.code()));
    }

    @Test
    void execute_savesOperations_whenProvided() {
        Document doc = nativeDocumentAtRevision(1L);
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));
        DocumentSuggestion saved = DocumentSuggestion.create(documentId, workspaceId, projectId, 1L, "Rewrite");
        when(suggestionRepo.save(any())).thenReturn(saved);

        var op = new CreateSuggestionCommand.OperationItem("REPLACE", "block-1", null,
                "{\"type\":\"paragraph\",\"text\":\"new\"}", 0);
        var cmd = new CreateSuggestionCommand(projectId, documentId, 1L, "Rewrite", List.of(op));
        action.execute(cmd);

        verify(operationRepo).saveAll(any());
    }

    @Test
    void execute_noOperationSave_whenOperationListEmpty() {
        Document doc = nativeDocumentAtRevision(1L);
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));
        DocumentSuggestion saved = DocumentSuggestion.create(documentId, workspaceId, projectId, 1L, "Describe only");
        when(suggestionRepo.save(any())).thenReturn(saved);

        var cmd = new CreateSuggestionCommand(projectId, documentId, 1L, "Describe only", List.of());
        action.execute(cmd);

        verify(operationRepo, never()).saveAll(any());
    }

    @Test
    void execute_throwsNotFound_whenDocumentMissing() {
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.empty());

        var cmd = new CreateSuggestionCommand(projectId, documentId, 1L, "Fix", List.of());

        assertThatThrownBy(() -> action.execute(cmd))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.DOCUMENT_NOT_FOUND.code()));
    }

    private Document nativeDocumentAtRevision(long revisionNo) {
        return new Document(documentId, workspaceId, projectId, null, "SPEC", "D-1", "Title", null,
                com.company.scopery.modules.documenthub.document.domain.enums.DocumentStatus.DRAFT,
                "INTERNAL", null, false, null, null, null, null, null, 0, Instant.now(), Instant.now(),
                ContentMode.NATIVE, null, UUID.randomUUID(), revisionNo, 1, "chk",
                Instant.now(), null, null, null, null,
                com.company.scopery.modules.documenthub.document.domain.enums.ContentWidth.CENTERED, false);
    }
}
