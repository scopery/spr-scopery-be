package com.company.scopery.modules.documenthub.document.domain.model;

import com.company.scopery.modules.documenthub.document.domain.enums.ContentMode;
import com.company.scopery.modules.documenthub.document.domain.enums.DocumentStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentNativeEditorDomainTest {

    final UUID workspaceId = UUID.randomUUID();
    final UUID projectId = UUID.randomUUID();
    final UUID actorId = UUID.randomUUID();

    // ── isNativeEditable ──────────────────────────────────────────────────────

    @Test
    void isNativeEditable_trueForNativeMode() {
        Document doc = Document.create(workspaceId, projectId, null, "SPEC", "D-1", "Title", null, ContentMode.NATIVE);
        assertThat(doc.isNativeEditable()).isTrue();
    }

    @Test
    void isNativeEditable_trueForHybridMode() {
        Document doc = Document.create(workspaceId, projectId, null, "SPEC", "D-1", "Title", null, ContentMode.HYBRID);
        assertThat(doc.isNativeEditable()).isTrue();
    }

    @Test
    void isNativeEditable_falseForFileMode() {
        Document doc = Document.create(workspaceId, projectId, null, "SPEC", "D-1", "Title", null, ContentMode.FILE);
        assertThat(doc.isNativeEditable()).isFalse();
    }

    @Test
    void isNativeEditable_falseForDefaultMode() {
        Document doc = Document.create(workspaceId, projectId, null, "SPEC", "D-1", "Title", null);
        assertThat(doc.isNativeEditable()).isFalse();
        assertThat(doc.contentMode()).isEqualTo(ContentMode.FILE);
    }

    @Test
    void isNativeEditable_falseWhenContentModeNull_defaultsToFile() {
        Document doc = Document.create(workspaceId, projectId, null, "SPEC", "D-1", "Title", null, null);
        assertThat(doc.contentMode()).isEqualTo(ContentMode.FILE);
        assertThat(doc.isNativeEditable()).isFalse();
    }

    // ── isArchived ────────────────────────────────────────────────────────────

    @Test
    void isArchived_falseForDraft() {
        Document draft = Document.create(workspaceId, projectId, null, "SPEC", "D-1", "Title", null);
        assertThat(draft.isArchived()).isFalse();
    }

    @Test
    void isArchived_falseForApproved() {
        Document approved = Document.create(workspaceId, projectId, null, "SPEC", "D-1", "Title", null).approve(actorId);
        assertThat(approved.isArchived()).isFalse();
    }

    @Test
    void isArchived_trueForArchived() {
        Document archived = Document.create(workspaceId, projectId, null, "SPEC", "D-1", "Title", null).archive(actorId);
        assertThat(archived.isArchived()).isTrue();
        assertThat(archived.status()).isEqualTo(DocumentStatus.ARCHIVED);
    }

    // ── withNativeContentSaved ────────────────────────────────────────────────

    @Test
    void withNativeContentSaved_transitionsFileModeToNative() {
        Document fileDoc = Document.create(workspaceId, projectId, null, "SPEC", "D-1", "Title", null, ContentMode.FILE);
        assertThat(fileDoc.contentMode()).isEqualTo(ContentMode.FILE);

        UUID revisionId = UUID.randomUUID();
        Document updated = fileDoc.withNativeContentSaved(revisionId, 1L, "abc123", 1, actorId);

        assertThat(updated.contentMode()).isEqualTo(ContentMode.NATIVE);
        assertThat(updated.currentContentRevisionNo()).isEqualTo(1L);
        assertThat(updated.contentChecksum()).isEqualTo("abc123");
        assertThat(updated.editorSchemaVersion()).isEqualTo(1);
        assertThat(updated.contentUpdatedBy()).isEqualTo(actorId);
        assertThat(updated.currentContentRevisionId()).isEqualTo(revisionId);
    }

    @Test
    void withNativeContentSaved_keepNativeModeIfAlreadyNative() {
        Document nativeDoc = Document.create(workspaceId, projectId, null, "SPEC", "D-1", "Title", null, ContentMode.NATIVE);
        Document updated = nativeDoc.withNativeContentSaved(UUID.randomUUID(), 2L, "xyz", 1, actorId);
        assertThat(updated.contentMode()).isEqualTo(ContentMode.NATIVE);
    }

    @Test
    void withNativeContentSaved_updatesRevisionNumberCorrectly() {
        Document doc = Document.create(workspaceId, projectId, null, "SPEC", "D-1", "Title", null, ContentMode.NATIVE);
        assertThat(doc.currentContentRevisionNo()).isEqualTo(0L);

        Document updated = doc.withNativeContentSaved(UUID.randomUUID(), 5L, "ck", 1, actorId);
        assertThat(updated.currentContentRevisionNo()).isEqualTo(5L);
    }

    // ── withClientVisible ─────────────────────────────────────────────────────

    @Test
    void withClientVisible_setsFlag() {
        Document doc = Document.create(workspaceId, projectId, null, "SPEC", "D-1", "Title", null);
        assertThat(doc.clientVisible()).isFalse();

        Document visible = doc.withClientVisible(true);
        assertThat(visible.clientVisible()).isTrue();

        Document hidden = visible.withClientVisible(false);
        assertThat(hidden.clientVisible()).isFalse();
    }

    // ── approve guards ────────────────────────────────────────────────────────

    @Test
    void approve_throwsIllegalStateWhenAlreadyApproved() {
        Document approved = Document.create(workspaceId, projectId, null, "SPEC", "D-1", "Title", null).approve(actorId);
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () -> approved.approve(actorId));
    }

    @Test
    void approve_throwsIllegalStateWhenArchived() {
        Document archived = Document.create(workspaceId, projectId, null, "SPEC", "D-1", "Title", null).archive(actorId);
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () -> archived.approve(actorId));
    }

    @Test
    void create_defaultsRevisionNoToZero() {
        Document doc = Document.create(workspaceId, projectId, null, "SPEC", "D-1", "Title", null);
        assertThat(doc.currentContentRevisionNo()).isEqualTo(0L);
    }

    @Test
    void create_defaultsClientVisibleToFalse() {
        Document doc = Document.create(workspaceId, projectId, null, "SPEC", "D-1", "Title", null);
        assertThat(doc.clientVisible()).isFalse();
    }
}
