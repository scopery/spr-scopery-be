package com.company.scopery.modules.airecommendation.application.jobs;

import com.company.scopery.modules.airecommendation.domain.enums.SuggestionStatus;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestion;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionRepository;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationEntityTypes;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Component
public class RecommendationExpiryJob {

    private static final Logger log = LoggerFactory.getLogger(RecommendationExpiryJob.class);
    private static final String SOURCE_SYSTEM = "SCOPERY_AI_RECOMMENDATION";
    private static final String EVENT_EXPIRED = "AI_SUGGESTION_EXPIRED";
    private static final int BATCH_SIZE = 500;

    private final AiSuggestionRepository suggestionRepository;
    private final TransactionalOutboxService outbox;

    public RecommendationExpiryJob(AiSuggestionRepository suggestionRepository,
                                    TransactionalOutboxService outbox) {
        this.suggestionRepository = suggestionRepository;
        this.outbox = outbox;
    }

    @Scheduled(cron = "${scopery.ai.recommendation.expiry-cron:0 0 * * * *}")
    @Transactional
    public void expireSuggestions() {
        OffsetDateTime now = OffsetDateTime.now();
        List<AiSuggestion> expired = suggestionRepository.findExpiredAndActive(now, BATCH_SIZE);

        if (expired.isEmpty()) return;

        log.info("RecommendationExpiryJob: expiring {} suggestions", expired.size());
        for (AiSuggestion suggestion : expired) {
            AiSuggestion updated = withExpired(suggestion, now);
            suggestionRepository.save(updated);

            outbox.enqueue(AiRecommendationEntityTypes.SUGGESTION, suggestion.id(),
                    EVENT_EXPIRED, SOURCE_SYSTEM, 1,
                    Map.of("suggestionRef", "p43:" + suggestion.id(),
                            "fromStatus", suggestion.status().name()));
        }
    }

    private AiSuggestion withExpired(AiSuggestion s, OffsetDateTime now) {
        return new AiSuggestion(
                s.id(), s.runId(), s.policyId(), s.workspaceId(), s.projectId(), s.sourceSystem(),
                s.legacyPhase21SuggestionId(), s.packCode(), s.detectorCode(), s.suggestionType(),
                s.schemaCode(), s.schemaVersion(), s.category(), s.severity(), SuggestionStatus.EXPIRED,
                s.title(), s.summary(), s.reason(), s.targetEntityType(), s.targetEntityId(),
                s.targetVersionToken(), s.confidenceMethod(), s.confidenceValue(), s.confidenceLabel(),
                s.riskLevel(), s.dedupKey(), s.payloadHash(), s.occurrenceCount(),
                s.originConversationId(), s.originMessageId(), s.originTurnId(),
                s.supersedesSuggestionId(), s.supersededBySuggestionId(),
                s.firstObservedAt(), s.lastObservedAt(),
                s.viewedAt(), s.editedAt(), s.acceptedAt(), s.rejectedAt(), s.suppressedAt(),
                s.expiresAt(), s.staleAt(), s.staleReasonCode(), s.createdAt(), now, s.version() + 1);
    }
}
