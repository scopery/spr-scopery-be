package com.company.scopery.modules.aiassistant.application.service;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AiAssistantIntentClassificationService {

    public enum QueryIntent {
        GENERAL,
        PROJECT_GROUNDED
    }

    private static final Set<String> GENERAL_TOKENS = Set.of(
            "hi", "hello", "hey",
            "chào", "xin chào", "xinchào",
            "cảm ơn", "camon", "cảm on",
            "thanks", "thank you", "thankyou",
            "ok", "okay", "oke", "sure", "got it",
            "bye", "goodbye", "tạm biệt", "tambiet",
            "nice", "great", "cool", "👋", "🙏"
    );

    /**
     * Classify whether a user query needs project knowledge retrieval.
     * Short conversational phrases → GENERAL (skip retrieval).
     * Everything else in a project context → PROJECT_GROUNDED.
     */
    public QueryIntent classify(String query, boolean hasProjectContext) {
        if (!hasProjectContext) return QueryIntent.GENERAL;
        if (query == null || query.isBlank()) return QueryIntent.GENERAL;

        String normalized = query.strip().toLowerCase();
        if (normalized.length() <= 30 && isGeneralPhrase(normalized)) {
            return QueryIntent.GENERAL;
        }
        return QueryIntent.PROJECT_GROUNDED;
    }

    private boolean isGeneralPhrase(String normalized) {
        String cleaned = normalized.replaceAll("[!?.\\s]+$", "").trim();
        if (GENERAL_TOKENS.contains(cleaned)) return true;
        // "thank you very much" style
        for (String token : GENERAL_TOKENS) {
            if (cleaned.startsWith(token + " ") || cleaned.equals(token)) return true;
        }
        return false;
    }
}
