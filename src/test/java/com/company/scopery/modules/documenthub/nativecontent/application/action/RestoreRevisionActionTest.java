package com.company.scopery.modules.documenthub.nativecontent.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.documenthub.document.domain.enums.ContentMode;
import com.company.scopery.modules.documenthub.document.domain.enums.ContentWidth;
import com.company.scopery.modules.documenthub.document.domain.enums.DocumentStatus;
import com.company.scopery.modules.documenthub.document.domain.model.Document;
import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.nativecontent.application.command.RestoreRevisionCommand;
import com.company.scopery.modules.documenthub.nativecontent.application.command.SaveDocumentContentCommand;
import com.company.scopery.modules.documenthub.nativecontent.application.response.DocumentContentResponse;
import com.company.scopery.modules.documenthub.nativecontent.domain.enums.RevisionType;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentRevision;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentRevisionRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubErrorCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests BR-NDE-030: restore creates a new revision (not mutate the existing one).
 */
@ExtendWith(MockitoExtension.class)
class RestoreRevisionActionTest {

    @Mock DocumentRepository documents;
    @Mock DocumentRevisionRepository revisionRepo;
    @Mock DocumentHubAuthorizationService authorization;
    @Mock DocumentHubActivityLogger activityLogger;
    @Mock SaveDocumentContentAction saveContent;

    RestoreRevisionAction action;

    final UUID projectId = UUID.randomUUID();
    final UUID documentId = UUID.randomUUID();
    final UUID workspaceId = UUID.randomUUID();
    final String oldAst = "{\"type\":\"doc\",\"content\":[{\"type\":\"paragraph\",\"text\":\"v1 content\"}]}";

    @BeforeEach
    void setUp() {
        action = new RestoreRevisionAction(documents, revisionRepo, authorization, activityLogger, saveContent);
    }

    @Test
    void execute_delegatesToSaveContent_withRestoreRevisionType() {
        // BR-NDE-030: restoring revision 1 when at revision 3 creates revision 4 (RESTORE type)
        Document doc = nativeDocumentAtRevision(3L);
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));

        DocumentRevision targetRevision = stubRevision(1L, oldAst);
        when(revisionRepo.findByDocumentIdAndRevisionNo(documentId, 1L)).thenReturn(Optional.of(targetRevision));

        DocumentContentResponse savedResponse = stubContentResponse(4L);
        when(saveContent.execute(any())).thenReturn(savedResponse);

        var cmd = new RestoreRevisionCommand(projectId, documentId, 1L);
        var response = action.execute(cmd);

        // BR-NDE-030: new revision number returned (one higher than current)
        assertThat(response.revisionNo()).isEqualTo(4L);

        // Verify SaveDocumentContentAction called with RESTORE type and target AST
        ArgumentCaptor<SaveDocumentContentCommand> captor = ArgumentCaptor.forClass(SaveDocumentContentCommand.class);
        verify(saveContent).execute(captor.capture());
        SaveDocumentContentCommand savedCmd = captor.getValue();

        assertThat(savedCmd.ast()).isEqualTo(oldAst);
        assertThat(savedCmd.revisionType()).isEqualTo(RevisionType.RESTORE);
        assertThat(savedCmd.expectedBaseRevisionNo()).isEqualTo(3L);
        assertThat(savedCmd.documentId()).isEqualTo(documentId);
        assertThat(savedCmd.projectId()).isEqualTo(projectId);
    }

    @Test
    void execute_throwsNotFound_whenRevisionMissing() {
        Document doc = nativeDocumentAtRevision(3L);
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));
        when(revisionRepo.findByDocumentIdAndRevisionNo(documentId, 99L)).thenReturn(Optional.empty());

        var cmd = new RestoreRevisionCommand(projectId, documentId, 99L);

        assertThatThrownBy(() -> action.execute(cmd))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.CONTENT_NOT_FOUND.code()));
        verify(saveContent, never()).execute(any());
    }

    @Test
    void execute_throwsNotFound_whenDocumentMissing() {
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> action.execute(new RestoreRevisionCommand(projectId, documentId, 1L)))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.DOCUMENT_NOT_FOUND.code()));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Document nativeDocumentAtRevision(long revisionNo) {
        return new Document(documentId, workspaceId, projectId, null, "SPEC", "D-1", "Title", null,
                DocumentStatus.DRAFT, "INTERNAL", null, false, null, null, null, null, null, 0,
                Instant.now(), Instant.now(),
                ContentMode.NATIVE, null, UUID.randomUUID(), revisionNo, 1, "chk",
                Instant.now(), null, null, null, null, ContentWidth.CENTERED, false);
    }

    private DocumentRevision stubRevision(long revisionNo, String ast) {
        return new DocumentRevision(UUID.randomUUID(), documentId, workspaceId, projectId,
                revisionNo, RevisionType.MANUAL, ast, "v1 content", "ck", 1, 2, 10, null, Instant.now());
    }

    private DocumentContentResponse stubContentResponse(long revisionNo) {
        return new DocumentContentResponse(UUID.randomUUID(), documentId, revisionNo,
                oldAst, "ck", 1, 2, 10, Instant.now(), null, Instant.now(), Instant.now());
    }
}
