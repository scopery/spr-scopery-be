package com.company.scopery.modules.aiplanning.shared.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(28)
public class AiPlanningEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    public static final String SOURCE_SYSTEM = "SCOPERY_AI_PLANNING";
    public static final String OWNER_MODULE = "AI_PLANNING";

    private final EventDefinitionRepository eventDefinitionRepository;

    public AiPlanningEventDefinitionSeedInitializer(EventDefinitionRepository eventDefinitionRepository) {
        this.eventDefinitionRepository = eventDefinitionRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (SeedEvent seed : EVENTS) {
            EventDefinitionSeedSupport.findOrCreate(
                    eventDefinitionRepository, SOURCE_SYSTEM, seed.code(), seed.name(),
                    seed.description(), EventDataClassification.INTERNAL, OWNER_MODULE);
        }
    }

    private record SeedEvent(String code, String name, String description) {}

    static final List<SeedEvent> EVENTS = List.of(
            new SeedEvent("AI_PLANNING_RUN_CREATED", "AI Planning Run Created", "AI planning run created"),
            new SeedEvent("AI_PLANNING_RUN_STARTED", "AI Planning Run Started", "AI planning run started"),
            new SeedEvent("AI_PLANNING_RUN_COMPLETED", "AI Planning Run Completed", "AI planning run completed"),
            new SeedEvent("AI_PLANNING_RUN_FAILED", "AI Planning Run Failed", "AI planning run failed"),
            new SeedEvent("AI_PLANNING_RUN_CANCELLED", "AI Planning Run Cancelled", "AI planning run cancelled"),
            new SeedEvent("AI_PLANNING_CONTEXT_SNAPSHOT_CREATED", "AI Context Snapshot Created", "Context snapshot created"),
            new SeedEvent("AI_PLANNING_SUGGESTION_GENERATED", "AI Suggestion Generated", "Suggestion generated"),
            new SeedEvent("AI_PLANNING_SUGGESTION_REVIEW_STARTED", "AI Suggestion Review Started", "Suggestion review started"),
            new SeedEvent("AI_PLANNING_SUGGESTION_ACCEPTED", "AI Suggestion Accepted", "Suggestion accepted"),
            new SeedEvent("AI_PLANNING_SUGGESTION_REJECTED", "AI Suggestion Rejected", "Suggestion rejected"),
            new SeedEvent("AI_PLANNING_SUGGESTION_ARCHIVED", "AI Suggestion Archived", "Suggestion archived"),
            new SeedEvent("AI_PLANNING_SUGGESTION_APPLIED", "AI Suggestion Applied", "Suggestion applied"),
            new SeedEvent("AI_PLANNING_ITEM_ACCEPTED", "AI Suggestion Item Accepted", "Suggestion item accepted"),
            new SeedEvent("AI_PLANNING_ITEM_REJECTED", "AI Suggestion Item Rejected", "Suggestion item rejected"),
            new SeedEvent("AI_PLANNING_ITEM_APPLIED", "AI Suggestion Item Applied", "Suggestion item applied"),
            new SeedEvent("AI_PLANNING_APPLY_FAILED", "AI Planning Apply Failed", "Suggestion apply failed")
    );
}
