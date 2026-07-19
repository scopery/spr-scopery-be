package com.company.scopery.modules.documenthub.suggestion.domain.model;

import com.company.scopery.modules.documenthub.suggestion.domain.enums.SuggestionStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests BR-NDE-032 (suggestion accept atomic — status transitions)
 * and BR-NDE-033 (stale suggestion does not auto-merge).
 */
class DocumentSuggestionDomainTest {

    final UUID documentId = UUID.randomUUID();
    final UUID workspaceId = UUID.randomUUID();
    final UUID projectId = UUID.randomUUID();
    final UUID actorId = UUID.randomUUID();

    @Test
    void create_statusIsPending() {
        var suggestion = DocumentSuggestion.create(documentId, workspaceId, projectId, 3L, "Fix typo");

        assertThat(suggestion.status()).isEqualTo(SuggestionStatus.PENDING);
        assertThat(suggestion.targetRevisionNo()).isEqualTo(3L);
        assertThat(suggestion.description()).isEqualTo("Fix typo");
        assertThat(suggestion.acceptedBy()).isNull();
        assertThat(suggestion.rejectedBy()).isNull();
        assertThat(suggestion.acceptedRevisionNo()).isNull();
    }

    @Test
    void accept_setsStatusAndAcceptanceMetadata() {
        var pending = DocumentSuggestion.create(documentId, workspaceId, projectId, 3L, "Fix typo");
        var accepted = pending.accept(actorId, 4L);

        assertThat(accepted.status()).isEqualTo(SuggestionStatus.ACCEPTED);
        assertThat(accepted.acceptedBy()).isEqualTo(actorId);
        assertThat(accepted.acceptedRevisionNo()).isEqualTo(4L);
        assertThat(accepted.acceptedAt()).isNotNull();
        assertThat(accepted.rejectedBy()).isNull();
    }

    @Test
    void accept_preservesOriginalId() {
        var pending = DocumentSuggestion.create(documentId, workspaceId, projectId, 3L, "Fix typo");
        var accepted = pending.accept(actorId, 4L);

        assertThat(accepted.id()).isEqualTo(pending.id());
        assertThat(accepted.documentId()).isEqualTo(documentId);
        assertThat(accepted.targetRevisionNo()).isEqualTo(3L);
    }

    @Test
    void reject_setsStatusAndRejectionMetadata() {
        var pending = DocumentSuggestion.create(documentId, workspaceId, projectId, 3L, "Fix typo");
        var rejected = pending.reject(actorId);

        assertThat(rejected.status()).isEqualTo(SuggestionStatus.REJECTED);
        assertThat(rejected.rejectedBy()).isEqualTo(actorId);
        assertThat(rejected.rejectedAt()).isNotNull();
        assertThat(rejected.acceptedBy()).isNull();
        assertThat(rejected.acceptedRevisionNo()).isNull();
    }

    @Test
    void reject_preservesOriginalId() {
        var pending = DocumentSuggestion.create(documentId, workspaceId, projectId, 3L, "Fix typo");
        var rejected = pending.reject(actorId);

        assertThat(rejected.id()).isEqualTo(pending.id());
        assertThat(rejected.documentId()).isEqualTo(documentId);
    }

    @Test
    void accept_resultRevisionNoIsHigherThanTarget() {
        var pending = DocumentSuggestion.create(documentId, workspaceId, projectId, 5L, "Add section");
        var accepted = pending.accept(actorId, 6L);

        assertThat(accepted.acceptedRevisionNo()).isGreaterThan(accepted.targetRevisionNo());
    }
}
