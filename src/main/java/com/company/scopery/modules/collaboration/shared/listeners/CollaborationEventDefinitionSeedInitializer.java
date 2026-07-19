package com.company.scopery.modules.collaboration.shared.listeners;
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
@Order(37)
public class CollaborationEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    public static final String SOURCE_SYSTEM = "SCOPERY_COLLABORATION";
    public static final String OWNER_MODULE = "COLLABORATION";
    private final EventDefinitionRepository eventDefinitionRepository;
    public CollaborationEventDefinitionSeedInitializer(EventDefinitionRepository eventDefinitionRepository) {
        this.eventDefinitionRepository = eventDefinitionRepository;
    }
    @Override @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (SeedEvent seed : EVENTS) {
            EventDefinitionSeedSupport.findOrCreate(
                    eventDefinitionRepository, SOURCE_SYSTEM, seed.code(), seed.name(),
                    seed.description(), EventDataClassification.INTERNAL, OWNER_MODULE);
        }
    }
    private record SeedEvent(String code, String name, String description) {}
    static final List<SeedEvent> EVENTS = List.of(
            new SeedEvent("MEETING_CREATED", "Meeting Created", "Meeting created"),
            new SeedEvent("MEETING_COMPLETED", "Meeting Completed", "Meeting completed"),
            new SeedEvent("MEETING_CANCELLED", "Meeting Cancelled", "Meeting cancelled"),
            new SeedEvent("MEETING_MINUTES_APPROVED", "Meeting Minutes Approved", "Minutes approved"),
            new SeedEvent("MEETING_ACTION_ITEM_CREATED", "Meeting Action Item Created", "Action item created"),
            new SeedEvent("MEETING_ACTION_ITEM_COMPLETED", "Meeting Action Item Completed", "Action item completed"),
            new SeedEvent("COMMENT_THREAD_CREATED", "Comment Thread Created", "Comment thread created"),
            new SeedEvent("COMMENT_CREATED", "Comment Created", "Comment created"),
            new SeedEvent("MENTION_CREATED", "Mention Created", "Mention created")
    );
}
