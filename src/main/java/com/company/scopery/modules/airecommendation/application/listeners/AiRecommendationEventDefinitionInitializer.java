package com.company.scopery.modules.airecommendation.application.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(51)
public class AiRecommendationEventDefinitionInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(AiRecommendationEventDefinitionInitializer.class);
    public static final String SOURCE_SYSTEM = "SCOPERY_AI_RECOMMENDATION";
    public static final String OWNER_MODULE  = "AIRECOMMENDATION";

    private final EventDefinitionRepository eventDefinitionRepository;

    public AiRecommendationEventDefinitionInitializer(EventDefinitionRepository eventDefinitionRepository) {
        this.eventDefinitionRepository = eventDefinitionRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (SeedEvent seed : EVENTS) {
            EventDefinitionSeedSupport.findOrCreate(
                    eventDefinitionRepository,
                    SOURCE_SYSTEM,
                    seed.code(),
                    seed.name(),
                    seed.description(),
                    EventDataClassification.INTERNAL,
                    OWNER_MODULE);
        }
        log.info("[AiRecommendationEventDefinitionSeed] Seeded {} AI recommendation events", EVENTS.size());
    }

    private record SeedEvent(String code, String name, String description) {}

    private static final List<SeedEvent> EVENTS = List.of(
            new SeedEvent("AI_RECOMMENDATION_RUN_REQUESTED",
                    "AI Recommendation Run Requested",
                    "A recommendation run was requested"),
            new SeedEvent("AI_RECOMMENDATION_RUN_STARTED",
                    "AI Recommendation Run Started",
                    "A recommendation run started execution"),
            new SeedEvent("AI_RECOMMENDATION_RUN_SUCCEEDED",
                    "AI Recommendation Run Succeeded",
                    "A recommendation run completed successfully"),
            new SeedEvent("AI_RECOMMENDATION_RUN_PARTIAL",
                    "AI Recommendation Run Partial",
                    "A recommendation run completed with partial detector failures"),
            new SeedEvent("AI_RECOMMENDATION_RUN_FAILED",
                    "AI Recommendation Run Failed",
                    "A recommendation run failed"),
            new SeedEvent("AI_SUGGESTION_GENERATED",
                    "AI Suggestion Generated",
                    "A new AI suggestion was generated"),
            new SeedEvent("AI_SUGGESTION_DEDUPLICATED",
                    "AI Suggestion Deduplicated",
                    "A duplicate AI suggestion occurrence was recorded"),
            new SeedEvent("AI_SUGGESTION_VIEWED",
                    "AI Suggestion Viewed",
                    "An AI suggestion was viewed"),
            new SeedEvent("AI_SUGGESTION_EDITED",
                    "AI Suggestion Edited",
                    "An AI suggestion's proposed items were edited"),
            new SeedEvent("AI_SUGGESTION_ACCEPTED",
                    "AI Suggestion Accepted",
                    "An AI suggestion was accepted (no domain mutation in Phase 43)"),
            new SeedEvent("AI_SUGGESTION_REJECTED",
                    "AI Suggestion Rejected",
                    "An AI suggestion was rejected"),
            new SeedEvent("AI_SUGGESTION_SUPPRESSED",
                    "AI Suggestion Suppressed",
                    "An AI suggestion was suppressed"),
            new SeedEvent("AI_SUGGESTION_EXPIRED",
                    "AI Suggestion Expired",
                    "An AI suggestion was marked expired by the expiry job"),
            new SeedEvent("AI_SUGGESTION_STALE",
                    "AI Suggestion Stale",
                    "An AI suggestion was marked stale due to target entity change"),
            new SeedEvent("AI_SUGGESTION_FEEDBACK_SUBMITTED",
                    "AI Suggestion Feedback Submitted",
                    "Helpfulness feedback was submitted for an AI suggestion"),
            new SeedEvent("AI_SUGGESTION_PREPARE_APPLY_REQUESTED",
                    "AI Suggestion Prepare Apply Requested",
                    "A prepare-apply request was made for an accepted suggestion"),
            new SeedEvent("AI_SUPPRESSION_CREATED",
                    "AI Suppression Created",
                    "A suppression rule was created for an AI suggestion")
    );
}
