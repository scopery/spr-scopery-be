package com.company.scopery.modules.documenthub.document.application.action;

import com.company.scopery.common.exception.AppException;
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
 * Tests BR-NDE-010: client-visible document must pass classification validation.
 * Specifically: RESTRICTED classification blocks enabling client visibility.
 */
@ExtendWith(MockitoExtension.class)
class ToggleClientVisibilityActionTest {

    @Mock DocumentRepository documentRepo;
    @Mock DocumentHubAuthorizationService authorization;
    @Mock DocumentHubActivityLogger activityLogger;

    ToggleClientVisibilityAction action;

    final UUID projectId = UUID.randomUUID();
    final UUID documentId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        action = new ToggleClientVisibilityAction(documentRepo, authorization, activityLogger);
    }

    @Test
    void enable_succeeds_whenClassificationIsInternal() {
        Document doc = documentWithClassification("INTERNAL");
        when(documentRepo.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));
        when(documentRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        action.execute(projectId, documentId, true);

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
        verify(documentRepo).save(captor.capture());
        assertThat(captor.getValue().clientVisible()).isTrue();
    }

    @Test
    void enable_succeeds_whenClassificationIsPublic() {
        Document doc = documentWithClassification("PUBLIC");
        when(documentRepo.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));
        when(documentRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        action.execute(projectId, documentId, true);

        verify(documentRepo).save(any());
    }

    @Test
    void enable_throws_whenClassificationIsRestricted() {
        // BR-NDE-010: RESTRICTED documents cannot be made client-visible
        Document doc = documentWithClassification("RESTRICTED");
        when(documentRepo.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));

        assertThatThrownBy(() -> action.execute(projectId, documentId, true))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.CLIENT_VISIBILITY_NOT_ALLOWED.code()));

        verify(documentRepo, never()).save(any());
    }

    @Test
    void enable_throws_caseInsensitive_restricted() {
        // "restricted" lowercase should still be blocked
        Document doc = documentWithClassification("restricted");
        when(documentRepo.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));

        assertThatThrownBy(() -> action.execute(projectId, documentId, true))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.CLIENT_VISIBILITY_NOT_ALLOWED.code()));
    }

    @Test
    void disable_succeeds_evenIfClassificationIsRestricted() {
        // Disabling visibility is always allowed — no classification check
        Document doc = documentWithClassificationAndVisible("RESTRICTED", true);
        when(documentRepo.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.of(doc));
        when(documentRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        action.execute(projectId, documentId, false);

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
        verify(documentRepo).save(captor.capture());
        assertThat(captor.getValue().clientVisible()).isFalse();
    }

    @Test
    void throwsNotFound_whenDocumentMissing() {
        when(documentRepo.findByIdAndProjectId(documentId, projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> action.execute(projectId, documentId, true))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(DocumentHubErrorCatalog.DOCUMENT_NOT_FOUND.code()));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Document documentWithClassification(String classification) {
        return new Document(documentId, UUID.randomUUID(), projectId, null, "SPEC", "D-1", "Title", null,
                DocumentStatus.DRAFT, classification, null, false, null, null, null, null, null, 0,
                Instant.now(), Instant.now(),
                ContentMode.NATIVE, null, null, 0L, null, null, null, null, null, null, null,
                ContentWidth.CENTERED, false);
    }

    private Document documentWithClassificationAndVisible(String classification, boolean clientVisible) {
        return new Document(documentId, UUID.randomUUID(), projectId, null, "SPEC", "D-1", "Title", null,
                DocumentStatus.DRAFT, classification, null, false, null, null, null, null, null, 0,
                Instant.now(), Instant.now(),
                ContentMode.NATIVE, null, null, 0L, null, null, null, null, null, null, null,
                ContentWidth.CENTERED, clientVisible);
    }
}
