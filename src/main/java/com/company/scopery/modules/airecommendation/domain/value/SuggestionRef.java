package com.company.scopery.modules.airecommendation.domain.value;

import com.company.scopery.modules.airecommendation.domain.enums.SourceSystem;
import com.company.scopery.modules.airecommendation.shared.error.AiRecommendationExceptions;

import java.util.UUID;

public record SuggestionRef(SourceSystem sourceSystem, UUID uuid) {

    private static final String PREFIX_P43 = "p43:";
    private static final String PREFIX_P21 = "p21:";

    public static SuggestionRef parse(String raw) {
        if (raw == null || raw.isBlank()) {
            throw AiRecommendationExceptions.suggestionReferenceInvalid(String.valueOf(raw));
        }
        if (raw.startsWith(PREFIX_P43)) {
            return new SuggestionRef(SourceSystem.PHASE43, parseUuid(raw.substring(PREFIX_P43.length()), raw));
        }
        if (raw.startsWith(PREFIX_P21)) {
            return new SuggestionRef(SourceSystem.PHASE21, parseUuid(raw.substring(PREFIX_P21.length()), raw));
        }
        throw AiRecommendationExceptions.suggestionReferenceInvalid(raw);
    }

    public static SuggestionRef ofPhase43(UUID id) {
        return new SuggestionRef(SourceSystem.PHASE43, id);
    }

    public static SuggestionRef ofPhase21(UUID id) {
        return new SuggestionRef(SourceSystem.PHASE21, id);
    }

    public boolean isPhase43() {
        return sourceSystem == SourceSystem.PHASE43;
    }

    public boolean isPhase21() {
        return sourceSystem == SourceSystem.PHASE21;
    }

    @Override
    public String toString() {
        return (sourceSystem == SourceSystem.PHASE43 ? PREFIX_P43 : PREFIX_P21) + uuid;
    }

    private static UUID parseUuid(String raw, String original) {
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            throw AiRecommendationExceptions.suggestionReferenceInvalid(original);
        }
    }
}
