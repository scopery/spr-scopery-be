package com.company.scopery.modules.aiplanning.suggestion.domain.model;

import com.company.scopery.modules.aiplanning.suggestion.domain.enums.SuggestionStatus;
import com.company.scopery.modules.aiplanning.suggestion.domain.enums.SuggestionType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiPlanningSuggestionDomainTest {

    private final UUID actorId = UUID.randomUUID();

    private AiPlanningSuggestion generated() {
        return AiPlanningSuggestion.create(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                SuggestionType.MIXED_PLAN, "Title", "Summary", "Why", "HIGH", "{}");
    }

    @Test
    void create_startsAsGenerated() {
        assertThat(generated().status()).isEqualTo(SuggestionStatus.GENERATED);
    }

    @Test
    void startReview_fromGenerated_success() {
        assertThat(generated().startReview(actorId).status()).isEqualTo(SuggestionStatus.UNDER_REVIEW);
    }

    @Test
    void acceptSuggestion_valid_success() {
        AiPlanningSuggestion accepted = generated().startReview(actorId).accept(actorId);
        assertThat(accepted.status()).isEqualTo(SuggestionStatus.ACCEPTED);
        assertThat(accepted.reviewedBy()).isEqualTo(actorId);
        assertThat(accepted.reviewedAt()).isNotNull();
    }

    @Test
    void accept_fromGenerated_withoutReview_success() {
        assertThat(generated().accept(actorId).status()).isEqualTo(SuggestionStatus.ACCEPTED);
    }

    @Test
    void rejectSuggestion_requiresReason_optionalPolicy() {
        AiPlanningSuggestion rejected = generated().reject(actorId, "not useful");
        assertThat(rejected.status()).isEqualTo(SuggestionStatus.REJECTED);
        assertThat(rejected.rejectionReason()).isEqualTo("not useful");
        assertThat(rejected.rejectedBy()).isEqualTo(actorId);
    }

    @Test
    void rejectedSuggestion_cannotApply() {
        AiPlanningSuggestion rejected = generated().reject(actorId, "no");
        assertThatThrownBy(() -> rejected.markApplied(actorId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid suggestion status");
    }

    @Test
    void acceptedSuggestion_canApply() {
        AiPlanningSuggestion applied = generated().accept(actorId).markApplied(actorId);
        assertThat(applied.status()).isEqualTo(SuggestionStatus.APPLIED);
        assertThat(applied.appliedBy()).isEqualTo(actorId);
        assertThat(applied.appliedAt()).isNotNull();
    }

    @Test
    void markPartiallyApplied_fromAccepted() {
        assertThat(generated().accept(actorId).markPartiallyApplied(actorId).status())
                .isEqualTo(SuggestionStatus.PARTIALLY_APPLIED);
    }

    @Test
    void archive_fromAnyReviewableState() {
        assertThat(generated().archive(actorId).status()).isEqualTo(SuggestionStatus.ARCHIVED);
    }

    @Test
    void startReview_fromAccepted_rejected() {
        AiPlanningSuggestion accepted = generated().accept(actorId);
        assertThatThrownBy(() -> accepted.startReview(actorId))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void accept_fromApplied_rejected() {
        AiPlanningSuggestion applied = generated().accept(actorId).markApplied(actorId);
        assertThatThrownBy(() -> applied.accept(actorId))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void suggestionGenerated_noDomainMutationFields() {
        AiPlanningSuggestion s = generated();
        assertThat(s.appliedAt()).isNull();
        assertThat(s.appliedBy()).isNull();
        assertThat(s.rejectedAt()).isNull();
    }
}
