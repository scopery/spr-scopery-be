package com.company.scopery.modules.projectbaseline.shared.listeners;

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
@Order(25)
public class ProjectBaselineEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    public static final String SOURCE_SYSTEM = "SCOPERY_PROJECT_GOVERNANCE";
    public static final String OWNER_MODULE = "PROJECT_BASELINE";

    private final EventDefinitionRepository eventDefinitionRepository;

    public ProjectBaselineEventDefinitionSeedInitializer(EventDefinitionRepository eventDefinitionRepository) {
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
            new SeedEvent("PROJECT_BASELINE_CREATED", "Project Baseline Created", "A project baseline was created"),
            new SeedEvent("PROJECT_BASELINE_REFRESHED", "Project Baseline Refreshed", "Baseline snapshot refreshed"),
            new SeedEvent("PROJECT_BASELINE_VALIDATED", "Project Baseline Validated", "Baseline validated"),
            new SeedEvent("PROJECT_BASELINE_APPROVED", "Project Baseline Approved", "Baseline approved"),
            new SeedEvent("PROJECT_BASELINE_MARKED_CURRENT", "Project Baseline Marked Current", "Baseline marked current"),
            new SeedEvent("PROJECT_BASELINE_ARCHIVED", "Project Baseline Archived", "Baseline archived"),
            new SeedEvent("CHANGE_REQUEST_CREATED", "Change Request Created", "Change request created"),
            new SeedEvent("CHANGE_REQUEST_UPDATED", "Change Request Updated", "Change request updated"),
            new SeedEvent("CHANGE_REQUEST_ITEM_CREATED", "Change Request Item Created", "Change request item created"),
            new SeedEvent("CHANGE_REQUEST_ITEM_UPDATED", "Change Request Item Updated", "Change request item updated"),
            new SeedEvent("CHANGE_REQUEST_ITEM_DELETED", "Change Request Item Deleted", "Change request item deleted"),
            new SeedEvent("CHANGE_IMPACT_UPDATED", "Change Impact Updated", "Change impact updated"),
            new SeedEvent("CHANGE_IMPACT_CALCULATED", "Change Impact Calculated", "Change impact calculated"),
            new SeedEvent("CHANGE_REQUEST_SUBMITTED", "Change Request Submitted", "Change request submitted"),
            new SeedEvent("CHANGE_REQUEST_APPROVED", "Change Request Approved", "Change request approved"),
            new SeedEvent("CHANGE_REQUEST_REJECTED", "Change Request Rejected", "Change request rejected"),
            new SeedEvent("CHANGE_REQUEST_CANCELLED", "Change Request Cancelled", "Change request cancelled"),
            new SeedEvent("CHANGE_REQUEST_APPLIED", "Change Request Applied", "Change request applied"),
            new SeedEvent("CHANGE_REQUEST_APPLY_FAILED", "Change Request Apply Failed", "Change request apply failed"),
            new SeedEvent("CHANGE_REQUEST_ARCHIVED", "Change Request Archived", "Change request archived"),
            new SeedEvent("CHANGE_ORDER_CREATED", "Change Order Created", "Change order created"),
            new SeedEvent("CHANGE_ORDER_UPDATED", "Change Order Updated", "Change order updated"),
            new SeedEvent("CHANGE_ORDER_SUBMITTED", "Change Order Submitted", "Change order submitted"),
            new SeedEvent("CHANGE_ORDER_APPROVED", "Change Order Approved", "Change order approved"),
            new SeedEvent("CHANGE_ORDER_REJECTED", "Change Order Rejected", "Change order rejected"),
            new SeedEvent("CHANGE_ORDER_ARCHIVED", "Change Order Archived", "Change order archived"),
            new SeedEvent("POST_BASELINE_EDIT_BLOCKED", "Post Baseline Edit Blocked", "Direct post-baseline edit blocked"),
            new SeedEvent("BASELINE_OVERRIDE_EDIT_USED", "Baseline Override Edit Used", "Baseline override edit used")
    );
}
