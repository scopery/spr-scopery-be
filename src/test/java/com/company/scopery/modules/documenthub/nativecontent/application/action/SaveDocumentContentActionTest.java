package com.company.scopery.modules.documenthub.nativecontent.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.documenthub.document.domain.enums.ContentMode;
import com.company.scopery.modules.documenthub.document.domain.model.Document;
import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.nativecontent.application.command.SaveDocumentContentCommand;
import com.company.scopery.modules.documenthub.nativecontent.application.service.BlockIndexExtractionService;
import com.company.scopery.modules.documenthub.nativecontent.application.service.MentionExtractionService;
import com.company.scopery.modules.documenthub.nativecontent.application.service.RelationExtractionService;
import com.company.scopery.modules.documenthub.nativecontent.domain.enums.RevisionType;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentContent;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentContentRepository;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentRevision;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentRevisionRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveDocumentContentActionTest {

    @Mock DocumentRepository documents;
    @Mock DocumentContentRepository contentRepo;
    @Mock DocumentRevisionRepository revisionRepo;
    @Mock DocumentHubAuthorizationService authorization;
    @Mock DocumentHubActivityLogger activityLogger;
    @Mock TransactionalOutboxService outbox;
    @Mock BlockIndexExtractionService blockIndexExtraction;
    @Mock MentionExtractionService mentionExtraction;
    @Mock RelationExtractionService relationExtraction;

    SaveDocumentContentAction action;

    final UUID projectId = UUID.randomUUID();
    final UUID documentId = UUID.randomUUID();
    final UUID workspaceId = UUID.randomUUID();
    final String ast = "{\"type\":\"doc\",\"content\":[{\"type\":\"paragraph\",\"text\":\"hello\"}]}";

    @BeforeEach
    void setUp() {
        action = new SaveDocumentContentAction(documents, contentRepo, revisionRepo, authorization,
                activityLogger, outbox, blockIndexExtraction, mentionExtraction, relationExtraction);
    }

    // ── Happy path: first save ────────────────────────────────────────────────

    @Test
    void execute_firstSave_createsRevision1() {
        Document nativeDoc = nativeDocument();
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(nativeDoc));
        when(contentRepo.findByDocumentId(documentId)).thenReturn(Optional.empty());
        DocumentRevision savedRevision = stubRevision(1L);
        when(revisionRepo.save(any())).thenReturn(savedRevision);
        DocumentContent savedContent = stubContent(1L);
        when(contentRepo.save(any())).thenReturn(savedContent);
        when(documents.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var cmd = new SaveDocumentContentCommand(projectId, documentId, ast, 0L, 1, null);
        var response = action.execute(cmd);

        assertThat(response.revisionNo()).isEqualTo(1L);
        verify(revisionRepo).save(any());
        verify(contentRepo).save(any());
        verify(documents).save(any());
        verify(outbox).enqueue(any(), eq(documentId), any(), any());
    }

    @Test
    void execute_firstSave_defaultsRevisionTypeToManual() {
        Document nativeDoc = nativeDocument();
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(nativeDoc));
        when(contentRepo.findByDocumentId(documentId)).thenReturn(Optional.empty());
        when(revisionRepo.save(any())).thenReturn(stubRevision(1L));
        when(contentRepo.save(any())).thenReturn(stubContent(1L));
        when(documents.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var cmd = new SaveDocumentContentCommand(projectId, documentId, ast, 0L, 1, null);
        action.execute(cmd);

        // revisionType null in command → MANUAL used inside action
        verify(revisionRepo).save(argThat(rev -> rev.revisionType() == RevisionType.MANUAL));
    }

    // ── No-op detection ───────────────────────────────────────────────────────

    @Test
    void execute_noOp_whenChecksumMatchesExistingContent() {
        Document nativeDoc = nativeDocument();
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(nativeDoc));

        // Compute the actual SHA-256 of 'ast' to simulate identical content
        String checksum = sha256(ast);
        DocumentContent existing = stubContentWithChecksum(1L, checksum);
        when(contentRepo.findByDocumentId(documentId)).thenReturn(Optional.of(existing));

        var cmd = new SaveDocumentContentCommand(projectId, documentId, ast, 1L, 1, null);
        var response = action.execute(cmd);

        assertThat(response.revisionNo()).isEqualTo(1L);
        verify(revisionRepo, never()).save(any());
        verify(contentRepo, never()).save(any());
        verify(outbox, never()).enqueue(any(), any(), any(), any());
    }

    // ── Optimistic locking ────────────────────────────────────────────────────

    @Test
    void execute_throwsConflict_whenExpectedRevisionMismatch() {
        Document nativeDoc = nativeDocument(); // currentContentRevisionNo = 0
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(nativeDoc));

        // Existing content has a different checksum (so no-op won't short-circuit)
        DocumentContent existing = stubContentWithChecksum(3L, "different-checksum");
        when(contentRepo.findByDocumentId(documentId)).thenReturn(Optional.of(existing));

        // expectedBaseRevisionNo = 1, but document.currentContentRevisionNo = 0 → conflict
        var cmd = new SaveDocumentContentCommand(projectId, documentId, ast, 1L, 1, null);

        assertThatThrownBy(() -> action.execute(cmd))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.CONTENT_OPTIMISTIC_LOCK_CONFLICT.code()));

        verify(revisionRepo, never()).save(any());
    }

    @Test
    void execute_noOptimisticLockCheck_forFirstSave_withNoExistingContent() {
        // When no existing content, expectedBaseRevisionNo check is skipped
        Document nativeDoc = nativeDocument();
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(nativeDoc));
        when(contentRepo.findByDocumentId(documentId)).thenReturn(Optional.empty());
        when(revisionRepo.save(any())).thenReturn(stubRevision(1L));
        when(contentRepo.save(any())).thenReturn(stubContent(1L));
        when(documents.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // expectedBaseRevisionNo = 99, but no existing content → no conflict thrown
        var cmd = new SaveDocumentContentCommand(projectId, documentId, ast, 99L, 1, null);
        var response = action.execute(cmd);

        assertThat(response).isNotNull();
    }

    // ── Guard: archived document ──────────────────────────────────────────────

    @Test
    void execute_throwsArchived_whenDocumentIsArchived() {
        Document archived = nativeDocument().archive(UUID.randomUUID());
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(archived));

        var cmd = new SaveDocumentContentCommand(projectId, documentId, ast, 0L, 1, null);

        assertThatThrownBy(() -> action.execute(cmd))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.DOCUMENT_ARCHIVED.code()));

        verify(revisionRepo, never()).save(any());
    }

    // ── Guard: locked document ────────────────────────────────────────────────

    @Test
    void execute_throwsLocked_whenDocumentIsLocked() {
        Document locked = lockedNativeDocument();
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(locked));

        var cmd = new SaveDocumentContentCommand(projectId, documentId, ast, 0L, 1, null);

        assertThatThrownBy(() -> action.execute(cmd))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.DOCUMENT_LOCKED_FOR_EDIT.code()));

        verify(revisionRepo, never()).save(any());
    }

    // ── Guard: FILE mode not editable ─────────────────────────────────────────

    @Test
    void execute_throwsContentNotSupported_forFileDocument() {
        Document fileDoc = fileDocument();
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(fileDoc));

        var cmd = new SaveDocumentContentCommand(projectId, documentId, ast, 0L, 1, null);

        assertThatThrownBy(() -> action.execute(cmd))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.CONTENT_NOT_SUPPORTED.code()));

        verify(revisionRepo, never()).save(any());
    }

    // ── Guard: document not found ─────────────────────────────────────────────

    @Test
    void execute_throwsNotFound_whenDocumentMissing() {
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.empty());

        var cmd = new SaveDocumentContentCommand(projectId, documentId, ast, 0L, 1, null);

        assertThatThrownBy(() -> action.execute(cmd))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.DOCUMENT_NOT_FOUND.code()));
    }

    // ── Second save increments revision ──────────────────────────────────────

    @Test
    void execute_secondSave_incremensToRevision2() {
        // Document already has revision 1
        Document nativeDoc = nativeDocumentAtRevision(1L);
        when(documents.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(nativeDoc));

        String differentAst = "{\"type\":\"doc\",\"content\":[{\"type\":\"paragraph\",\"text\":\"changed\"}]}";
        String existingChecksum = sha256("{\"type\":\"doc\",\"content\":[{\"type\":\"paragraph\",\"text\":\"old\"}]}");
        DocumentContent existing = stubContentWithChecksum(1L, existingChecksum);
        when(contentRepo.findByDocumentId(documentId)).thenReturn(Optional.of(existing));

        when(revisionRepo.save(any())).thenReturn(stubRevision(2L));
        when(contentRepo.save(any())).thenReturn(stubContent(2L));
        when(documents.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var cmd = new SaveDocumentContentCommand(projectId, documentId, differentAst, 1L, 1, null);
        var response = action.execute(cmd);

        assertThat(response.revisionNo()).isEqualTo(2L);
        verify(revisionRepo).save(argThat(rev -> rev.revisionNo() == 2L));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Document nativeDocument() {
        Document d = Document.create(workspaceId, projectId, null, "SPEC", "D-1", "Title", null, ContentMode.NATIVE);
        // id won't match documentId from field — need to wrap with correct id
        return new Document(documentId, workspaceId, projectId, null, "SPEC", "D-1", "Title", null,
                com.company.scopery.modules.documenthub.document.domain.enums.DocumentStatus.DRAFT,
                "INTERNAL", null, false, null, null, null, null, null, 0, Instant.now(), Instant.now(),
                ContentMode.NATIVE, null, null, 0L, null, null, null, null, null, null, null,
                com.company.scopery.modules.documenthub.document.domain.enums.ContentWidth.CENTERED, false);
    }

    private Document nativeDocumentAtRevision(long revisionNo) {
        return new Document(documentId, workspaceId, projectId, null, "SPEC", "D-1", "Title", null,
                com.company.scopery.modules.documenthub.document.domain.enums.DocumentStatus.DRAFT,
                "INTERNAL", null, false, null, null, null, null, null, 0, Instant.now(), Instant.now(),
                ContentMode.NATIVE, null, UUID.randomUUID(), revisionNo, 1, "some-checksum", Instant.now(), null,
                null, null, null,
                com.company.scopery.modules.documenthub.document.domain.enums.ContentWidth.CENTERED, false);
    }

    private Document fileDocument() {
        return new Document(documentId, workspaceId, projectId, null, "SPEC", "D-1", "Title", null,
                com.company.scopery.modules.documenthub.document.domain.enums.DocumentStatus.DRAFT,
                "INTERNAL", null, false, null, null, null, null, null, 0, Instant.now(), Instant.now(),
                ContentMode.FILE, null, null, 0L, null, null, null, null, null, null, null,
                com.company.scopery.modules.documenthub.document.domain.enums.ContentWidth.CENTERED, false);
    }

    private Document lockedNativeDocument() {
        return new Document(documentId, workspaceId, projectId, null, "SPEC", "D-1", "Title", null,
                com.company.scopery.modules.documenthub.document.domain.enums.DocumentStatus.DRAFT,
                "INTERNAL", null, true /* locked */, null, null, null, null, null, 0, Instant.now(), Instant.now(),
                ContentMode.NATIVE, null, null, 0L, null, null, null, null, null, null, null,
                com.company.scopery.modules.documenthub.document.domain.enums.ContentWidth.CENTERED, false);
    }

    private DocumentRevision stubRevision(long revisionNo) {
        return new DocumentRevision(UUID.randomUUID(), documentId, workspaceId, projectId,
                revisionNo, RevisionType.MANUAL, ast, "hello", sha256(ast), 1, 1, 5, null, Instant.now());
    }

    private DocumentContent stubContent(long revisionNo) {
        return new DocumentContent(UUID.randomUUID(), documentId, workspaceId, projectId,
                1, revisionNo, ast, "hello", 1, 5, sha256(ast), Instant.now(), null, 0, Instant.now(), Instant.now());
    }

    private DocumentContent stubContentWithChecksum(long revisionNo, String checksum) {
        return new DocumentContent(UUID.randomUUID(), documentId, workspaceId, projectId,
                1, revisionNo, ast, "hello", 1, 5, checksum, Instant.now(), null, 0, Instant.now(), Instant.now());
    }

    private String sha256(String input) {
        try {
            var digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
