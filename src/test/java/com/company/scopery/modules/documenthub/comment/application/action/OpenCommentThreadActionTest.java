package com.company.scopery.modules.documenthub.comment.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.documenthub.comment.application.command.OpenCommentThreadCommand;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentComment;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentCommentRepository;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentCommentThread;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentCommentThreadRepository;
import com.company.scopery.modules.documenthub.document.domain.enums.ContentMode;
import com.company.scopery.modules.documenthub.document.domain.enums.ContentWidth;
import com.company.scopery.modules.documenthub.document.domain.enums.DocumentStatus;
import com.company.scopery.modules.documenthub.document.domain.model.Document;
import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubErrorCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenCommentThreadActionTest {

    @Mock DocumentRepository documents;
    @Mock DocumentCommentThreadRepository threadRepo;
    @Mock DocumentCommentRepository commentRepo;
    @Mock DocumentHubAuthorizationService authorization;
    @Mock DocumentHubActivityLogger activityLogger;
    @Mock TransactionalOutboxService outbox;

    OpenCommentThreadAction action;

    final UUID projectId = UUID.randomUUID();
    final UUID documentId = UUID.randomUUID();
    final UUID workspaceId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        action = new OpenCommentThreadAction(documents, threadRepo, commentRepo,
                authorization, activityLogger, outbox);
    }

    @Test
    void execute_createsThreadAndFirstComment() {
        Document doc = nativeDocument();
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));

        DocumentCommentThread savedThread = DocumentCommentThread.create(documentId, workspaceId, projectId,
                "block-1", "highlighted text");
        when(threadRepo.save(any())).thenReturn(savedThread);

        DocumentComment savedComment = DocumentComment.create(savedThread.id(), documentId, "First comment");
        when(commentRepo.save(any())).thenReturn(savedComment);

        var cmd = new OpenCommentThreadCommand(projectId, documentId, "block-1", "highlighted text", "First comment");
        var response = action.execute(cmd);

        assertThat(response.blockId()).isEqualTo("block-1");
        assertThat(response.comments()).hasSize(1);
        assertThat(response.comments().get(0).body()).isEqualTo("First comment");
        verify(threadRepo).save(any());
        verify(commentRepo).save(any());
        verify(outbox).enqueue(any(), eq(savedThread.id()), any(), any());
    }

    @Test
    void execute_threadInitiallyOpen() {
        Document doc = nativeDocument();
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));

        DocumentCommentThread savedThread = DocumentCommentThread.create(documentId, workspaceId, projectId,
                "block-1", null);
        when(threadRepo.save(any())).thenReturn(savedThread);

        DocumentComment savedComment = DocumentComment.create(savedThread.id(), documentId, "My comment");
        when(commentRepo.save(any())).thenReturn(savedComment);

        var cmd = new OpenCommentThreadCommand(projectId, documentId, "block-1", null, "My comment");
        var response = action.execute(cmd);

        assertThat(response.status()).isEqualTo("OPEN");
    }

    @Test
    void execute_throwsNotFound_whenDocumentMissing() {
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.empty());

        var cmd = new OpenCommentThreadCommand(projectId, documentId, "block-1", null, "comment");

        assertThatThrownBy(() -> action.execute(cmd))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.DOCUMENT_NOT_FOUND.code()));

        verify(threadRepo, never()).save(any());
    }

    private Document nativeDocument() {
        return new Document(documentId, workspaceId, projectId, null, "SPEC", "D-1", "Title", null,
                DocumentStatus.DRAFT, "INTERNAL", null, false, null, null, null, null, null, 0,
                Instant.now(), Instant.now(),
                ContentMode.NATIVE, null, null, 0L, null, null, null, null, null, null, null,
                ContentWidth.CENTERED, false);
    }
}
