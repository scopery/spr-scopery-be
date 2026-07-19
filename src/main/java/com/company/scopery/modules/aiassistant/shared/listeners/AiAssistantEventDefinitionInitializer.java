package com.company.scopery.modules.aiassistant.shared.listeners;

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
@Order(15)
public class AiAssistantEventDefinitionInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(AiAssistantEventDefinitionInitializer.class);
    public static final String SOURCE_SYSTEM = "SCOPERY_AI_ASSISTANT";
    public static final String OWNER_MODULE = "AIASSISTANT";

    private final EventDefinitionRepository eventDefinitionRepository;

    public AiAssistantEventDefinitionInitializer(EventDefinitionRepository eventDefinitionRepository) {
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
        log.info("[AiAssistantEventDefinitionSeed] AI assistant event seeding complete ({} events)", EVENTS.size());
    }

    private record SeedEvent(String code, String name, String description) {}

    private static final List<SeedEvent> EVENTS = List.of(
            new SeedEvent("AI_CONVERSATION_CREATED",
                    "AI Conversation Created",
                    "A new AI assistant conversation was created"),
            new SeedEvent("AI_CONVERSATION_ARCHIVED",
                    "AI Conversation Archived",
                    "An AI assistant conversation was archived"),
            new SeedEvent("AI_CONVERSATION_DELETED",
                    "AI Conversation Deleted",
                    "An AI assistant conversation was soft-deleted"),
            new SeedEvent("AI_CONVERSATION_TITLE_UPDATED",
                    "AI Conversation Title Updated",
                    "The title of an AI assistant conversation was updated"),
            new SeedEvent("AI_MESSAGE_SENT",
                    "AI Message Sent",
                    "A user sent a message in an AI assistant conversation"),
            new SeedEvent("AI_MESSAGE_COMPLETED",
                    "AI Message Completed",
                    "An AI assistant response was generated and completed"),
            new SeedEvent("AI_MESSAGE_FAILED",
                    "AI Message Failed",
                    "An AI assistant response generation failed"),
            new SeedEvent("AI_MESSAGE_CANCELLED",
                    "AI Message Cancelled",
                    "An AI assistant response was cancelled by the user"),
            new SeedEvent("AI_MESSAGE_BLOCKED",
                    "AI Message Blocked",
                    "An AI assistant response was blocked by content policy"),
            new SeedEvent("AI_RETRIEVAL_COMPLETED",
                    "AI Retrieval Completed",
                    "Knowledge retrieval completed for an AI assistant turn"),
            new SeedEvent("AI_RETRIEVAL_FAILED",
                    "AI Retrieval Failed",
                    "Knowledge retrieval failed during an AI assistant turn"),
            new SeedEvent("AI_CITATION_ADDED",
                    "AI Citation Added",
                    "A citation was added to an AI assistant response"),
            new SeedEvent("AI_FEEDBACK_SUBMITTED",
                    "AI Feedback Submitted",
                    "A user submitted feedback on an AI assistant response"),
            new SeedEvent("AI_MEMORY_SUMMARIZED",
                    "AI Memory Summarized",
                    "A memory summary was created for a long conversation"),
            new SeedEvent("AI_QUOTA_EXCEEDED",
                    "AI Quota Exceeded",
                    "A user exceeded their daily AI assistant usage quota"),
            new SeedEvent("AI_GUIDE_REQUESTED",
                    "AI Guide Requested",
                    "A user requested a page/field/action explanation from the AI guide"),
            new SeedEvent("AI_CONTEXT_BUILT",
                    "AI Context Built",
                    "Server-authoritative context snapshot built for a turn"),
            new SeedEvent("AI_STREAM_STARTED",
                    "AI Stream Started",
                    "An AI assistant SSE stream was opened by a client"),
            new SeedEvent("AI_STREAM_COMPLETED",
                    "AI Stream Completed",
                    "An AI assistant SSE stream completed successfully"),
            new SeedEvent("AI_STREAM_REPLAYED",
                    "AI Stream Replayed",
                    "A client reconnected and replayed missed SSE stream events")
    );
}
