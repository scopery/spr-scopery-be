package com.company.scopery.modules.documenthub.comment.domain.model;

import com.company.scopery.modules.documenthub.comment.domain.enums.CommentThreadStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CommentDomainTest {

    final UUID documentId = UUID.randomUUID();
    final UUID workspaceId = UUID.randomUUID();
    final UUID projectId = UUID.randomUUID();
    final UUID actorId = UUID.randomUUID();

    // ── DocumentCommentThread ─────────────────────────────────────────────────

    @Test
    void create_thread_statusIsOpen() {
        var thread = DocumentCommentThread.create(documentId, workspaceId, projectId, "block-1", "highlighted text");

        assertThat(thread.status()).isEqualTo(CommentThreadStatus.OPEN);
        assertThat(thread.blockId()).isEqualTo("block-1");
        assertThat(thread.anchorText()).isEqualTo("highlighted text");
        assertThat(thread.resolvedBy()).isNull();
        assertThat(thread.resolvedAt()).isNull();
    }

    @Test
    void resolve_thread_statusIsResolved() {
        var thread = DocumentCommentThread.create(documentId, workspaceId, projectId, "block-1", "text");
        var resolved = thread.resolve(actorId);

        assertThat(resolved.status()).isEqualTo(CommentThreadStatus.RESOLVED);
        assertThat(resolved.resolvedBy()).isEqualTo(actorId);
        assertThat(resolved.resolvedAt()).isNotNull();
    }

    @Test
    void resolve_preservesOriginalId() {
        var thread = DocumentCommentThread.create(documentId, workspaceId, projectId, "block-1", "text");
        var resolved = thread.resolve(actorId);
        assertThat(resolved.id()).isEqualTo(thread.id());
    }

    // ── DocumentComment ────────────────────────────────────────────────────────

    @Test
    void create_comment_notDeleted() {
        UUID threadId = UUID.randomUUID();
        var comment = DocumentComment.create(threadId, documentId, "Hello world");

        assertThat(comment.body()).isEqualTo("Hello world");
        assertThat(comment.deletedAt()).isNull();
        assertThat(comment.isDeleted()).isFalse();
    }

    @Test
    void softDelete_setsDeletedAt() {
        UUID threadId = UUID.randomUUID();
        var comment = DocumentComment.create(threadId, documentId, "Hello");
        var deleted = comment.softDelete();

        assertThat(deleted.isDeleted()).isTrue();
        assertThat(deleted.deletedAt()).isNotNull();
        assertThat(deleted.id()).isEqualTo(comment.id());
        assertThat(deleted.body()).isEqualTo(comment.body());
    }

    @Test
    void softDelete_doesNotHardDelete() {
        // Body and ID should remain after soft-delete
        UUID threadId = UUID.randomUUID();
        var comment = DocumentComment.create(threadId, documentId, "Keep body");
        var deleted = comment.softDelete();

        assertThat(deleted.body()).isEqualTo("Keep body");
        assertThat(deleted.threadId()).isEqualTo(threadId);
        assertThat(deleted.documentId()).isEqualTo(documentId);
    }
}
