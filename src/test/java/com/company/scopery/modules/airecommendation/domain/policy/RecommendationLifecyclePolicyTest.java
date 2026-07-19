package com.company.scopery.modules.airecommendation.domain.policy;

import com.company.scopery.modules.airecommendation.domain.enums.SuggestionStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendationLifecyclePolicyTest {

    @Test
    void generated_canTransitionToViewed() {
        assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.GENERATED, SuggestionStatus.VIEWED)).isTrue();
    }

    @Test
    void generated_canTransitionToEdited() {
        assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.GENERATED, SuggestionStatus.EDITED)).isTrue();
    }

    @Test
    void generated_canTransitionToAccepted() {
        assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.GENERATED, SuggestionStatus.ACCEPTED)).isTrue();
    }

    @Test
    void generated_canTransitionToRejected() {
        assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.GENERATED, SuggestionStatus.REJECTED)).isTrue();
    }

    @Test
    void generated_canTransitionToSuppressed() {
        assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.GENERATED, SuggestionStatus.SUPPRESSED)).isTrue();
    }

    @Test
    void generated_cannotSelfTransition() {
        assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.GENERATED, SuggestionStatus.GENERATED)).isFalse();
    }

    @Test
    void viewed_canTransitionToEdited() {
        assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.VIEWED, SuggestionStatus.EDITED)).isTrue();
    }

    @Test
    void viewed_canTransitionToAccepted() {
        assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.VIEWED, SuggestionStatus.ACCEPTED)).isTrue();
    }

    @Test
    void viewed_cannotTransitionToGenerated() {
        assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.VIEWED, SuggestionStatus.GENERATED)).isFalse();
    }

    @Test
    void viewed_cannotSelfTransition() {
        assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.VIEWED, SuggestionStatus.VIEWED)).isFalse();
    }

    @Test
    void edited_canTransitionToAccepted() {
        assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.EDITED, SuggestionStatus.ACCEPTED)).isTrue();
    }

    @Test
    void edited_canTransitionToRejected() {
        assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.EDITED, SuggestionStatus.REJECTED)).isTrue();
    }

    @Test
    void edited_cannotTransitionToViewed() {
        assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.EDITED, SuggestionStatus.VIEWED)).isFalse();
    }

    @Test
    void accepted_canTransitionToExpired() {
        assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.ACCEPTED, SuggestionStatus.EXPIRED)).isTrue();
    }

    @Test
    void accepted_canTransitionToStale() {
        assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.ACCEPTED, SuggestionStatus.STALE)).isTrue();
    }

    @Test
    void accepted_canTransitionToSuperseded() {
        assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.ACCEPTED, SuggestionStatus.SUPERSEDED)).isTrue();
    }

    @Test
    void accepted_cannotTransitionToRejected() {
        assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.ACCEPTED, SuggestionStatus.REJECTED)).isFalse();
    }

    @Test
    void terminalStatus_cannotTransitionToAnything() {
        for (SuggestionStatus to : SuggestionStatus.values()) {
            assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.REJECTED, to)).isFalse();
            assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.SUPPRESSED, to)).isFalse();
            assertThat(RecommendationLifecyclePolicy.canTransitionTo(SuggestionStatus.EXPIRED, to)).isFalse();
        }
    }

    @Test
    void isActive_activeStatuses() {
        assertThat(RecommendationLifecyclePolicy.isActive(SuggestionStatus.GENERATED)).isTrue();
        assertThat(RecommendationLifecyclePolicy.isActive(SuggestionStatus.VIEWED)).isTrue();
        assertThat(RecommendationLifecyclePolicy.isActive(SuggestionStatus.EDITED)).isTrue();
        assertThat(RecommendationLifecyclePolicy.isActive(SuggestionStatus.ACCEPTED)).isTrue();
    }

    @Test
    void isActive_terminalStatuses_false() {
        assertThat(RecommendationLifecyclePolicy.isActive(SuggestionStatus.REJECTED)).isFalse();
        assertThat(RecommendationLifecyclePolicy.isActive(SuggestionStatus.SUPPRESSED)).isFalse();
        assertThat(RecommendationLifecyclePolicy.isActive(SuggestionStatus.EXPIRED)).isFalse();
        assertThat(RecommendationLifecyclePolicy.isActive(SuggestionStatus.STALE)).isFalse();
        assertThat(RecommendationLifecyclePolicy.isActive(SuggestionStatus.SUPERSEDED)).isFalse();
    }

    @Test
    void isTerminal_terminalStatuses() {
        assertThat(RecommendationLifecyclePolicy.isTerminal(SuggestionStatus.REJECTED)).isTrue();
        assertThat(RecommendationLifecyclePolicy.isTerminal(SuggestionStatus.SUPPRESSED)).isTrue();
        assertThat(RecommendationLifecyclePolicy.isTerminal(SuggestionStatus.EXPIRED)).isTrue();
        assertThat(RecommendationLifecyclePolicy.isTerminal(SuggestionStatus.STALE)).isTrue();
        assertThat(RecommendationLifecyclePolicy.isTerminal(SuggestionStatus.SUPERSEDED)).isTrue();
    }

    @Test
    void isTerminal_activeStatuses_false() {
        assertThat(RecommendationLifecyclePolicy.isTerminal(SuggestionStatus.GENERATED)).isFalse();
        assertThat(RecommendationLifecyclePolicy.isTerminal(SuggestionStatus.VIEWED)).isFalse();
        assertThat(RecommendationLifecyclePolicy.isTerminal(SuggestionStatus.ACCEPTED)).isFalse();
    }
}
