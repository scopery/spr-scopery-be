package com.company.scopery.modules.airecommendation.domain.policy;

import com.company.scopery.modules.airecommendation.domain.enums.SuggestionStatus;

import java.util.EnumSet;
import java.util.Set;

public final class RecommendationLifecyclePolicy {

    private static final Set<SuggestionStatus> ACTIVE_STATUSES =
            EnumSet.of(SuggestionStatus.GENERATED, SuggestionStatus.VIEWED,
                    SuggestionStatus.EDITED, SuggestionStatus.ACCEPTED);

    private static final Set<SuggestionStatus> TERMINAL_STATUSES =
            EnumSet.of(SuggestionStatus.REJECTED, SuggestionStatus.SUPPRESSED,
                    SuggestionStatus.EXPIRED, SuggestionStatus.STALE,
                    SuggestionStatus.SUPERSEDED);

    private RecommendationLifecyclePolicy() {}

    public static boolean canTransitionTo(SuggestionStatus from, SuggestionStatus to) {
        return switch (from) {
            case GENERATED -> to != SuggestionStatus.GENERATED;
            case VIEWED -> to != SuggestionStatus.GENERATED && to != SuggestionStatus.VIEWED;
            case EDITED -> !Set.of(SuggestionStatus.GENERATED, SuggestionStatus.VIEWED, SuggestionStatus.EDITED).contains(to);
            case ACCEPTED -> Set.of(SuggestionStatus.EXPIRED, SuggestionStatus.STALE, SuggestionStatus.SUPERSEDED).contains(to);
            default -> false;
        };
    }

    public static boolean isActive(SuggestionStatus status) {
        return ACTIVE_STATUSES.contains(status);
    }

    public static boolean isTerminal(SuggestionStatus status) {
        return TERMINAL_STATUSES.contains(status);
    }
}
