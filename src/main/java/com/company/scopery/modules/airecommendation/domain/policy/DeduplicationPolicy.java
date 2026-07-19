package com.company.scopery.modules.airecommendation.domain.policy;

import com.company.scopery.modules.airecommendation.domain.enums.SuggestionStatus;

import java.util.EnumSet;
import java.util.Set;

public final class DeduplicationPolicy {

    public static final Set<SuggestionStatus> ACTIVE_DEDUP_STATUSES =
            EnumSet.of(SuggestionStatus.GENERATED, SuggestionStatus.VIEWED,
                    SuggestionStatus.EDITED, SuggestionStatus.ACCEPTED);

    private DeduplicationPolicy() {}

    public static boolean isActiveForDedup(SuggestionStatus status) {
        return ACTIVE_DEDUP_STATUSES.contains(status);
    }
}
