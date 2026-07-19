package com.company.scopery.modules.ratecard.shared.listeners;

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
@Order(19)
public class RateCardEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    public static final String SOURCE_SYSTEM = "SCOPERY_RATE_CARD";
    public static final String OWNER_MODULE = "RATE_CARD";

    private final EventDefinitionRepository eventDefinitionRepository;

    public RateCardEventDefinitionSeedInitializer(EventDefinitionRepository eventDefinitionRepository) {
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
    }

    private record SeedEvent(String code, String name, String description) {}

    private static final List<SeedEvent> EVENTS = List.of(
            new SeedEvent("COST_ROLE_CREATED", "Cost Role Created", "A planning cost role was created"),
            new SeedEvent("COST_ROLE_UPDATED", "Cost Role Updated", "A planning cost role was updated"),
            new SeedEvent("COST_ROLE_ARCHIVED", "Cost Role Archived", "A planning cost role was archived"),
            new SeedEvent("COST_ROLE_ACTIVATED", "Cost Role Activated", "A planning cost role was activated"),
            new SeedEvent("COST_ROLE_DEACTIVATED", "Cost Role Deactivated", "A planning cost role was deactivated"),
            new SeedEvent("MEMBER_COST_ROLE_ASSIGNED", "Member Cost Role Assigned", "A workspace member was assigned a default cost role"),
            new SeedEvent("MEMBER_COST_ROLE_UPDATED", "Member Cost Role Updated", "A workspace member cost role assignment was updated"),
            new SeedEvent("MEMBER_COST_ROLE_ARCHIVED", "Member Cost Role Archived", "A workspace member cost role assignment was archived"),
            new SeedEvent("RATE_CARD_CREATED", "Rate Card Created", "A rate card was created"),
            new SeedEvent("RATE_CARD_UPDATED", "Rate Card Updated", "A rate card was updated"),
            new SeedEvent("RATE_CARD_ACTIVATED", "Rate Card Activated", "A rate card was activated"),
            new SeedEvent("RATE_CARD_DEACTIVATED", "Rate Card Deactivated", "A rate card was deactivated"),
            new SeedEvent("RATE_CARD_ARCHIVED", "Rate Card Archived", "A rate card was archived"),
            new SeedEvent("RATE_CARD_VERSION_CREATED", "Rate Card Version Created", "A rate card version was created"),
            new SeedEvent("RATE_CARD_VERSION_UPDATED", "Rate Card Version Updated", "A rate card version was updated"),
            new SeedEvent("RATE_CARD_VERSION_PUBLISHED", "Rate Card Version Published", "A rate card version was published"),
            new SeedEvent("RATE_CARD_VERSION_ARCHIVED", "Rate Card Version Archived", "A rate card version was archived"),
            new SeedEvent("RATE_CARD_VERSION_DUPLICATED", "Rate Card Version Duplicated", "A rate card version was duplicated"),
            new SeedEvent("RATE_CARD_LINE_CREATED", "Rate Card Line Created", "A rate card line was created"),
            new SeedEvent("RATE_CARD_LINE_UPDATED", "Rate Card Line Updated", "A rate card line was updated"),
            new SeedEvent("RATE_CARD_LINE_DELETED", "Rate Card Line Deleted", "A rate card line was deleted"),
            new SeedEvent("INFLATION_POLICY_CREATED", "Inflation Policy Created", "An inflation policy was created"),
            new SeedEvent("INFLATION_POLICY_UPDATED", "Inflation Policy Updated", "An inflation policy was updated"),
            new SeedEvent("INFLATION_POLICY_ACTIVATED", "Inflation Policy Activated", "An inflation policy was activated"),
            new SeedEvent("INFLATION_POLICY_DEACTIVATED", "Inflation Policy Deactivated", "An inflation policy was deactivated"),
            new SeedEvent("INFLATION_POLICY_ARCHIVED", "Inflation Policy Archived", "An inflation policy was archived"),
            new SeedEvent("RATE_RESOLVED", "Rate Resolved", "A rate was successfully resolved"),
            new SeedEvent("RATE_RESOLUTION_FAILED", "Rate Resolution Failed", "Rate resolution failed")
    );
}
