package com.company.scopery.modules.raid.shared.listeners;
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
@Order(31)
public class RaidEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {
    public static final String SOURCE_SYSTEM = "SCOPERY_RAID";
    public static final String OWNER_MODULE = "RAID";
    private final EventDefinitionRepository eventDefinitionRepository;
    public RaidEventDefinitionSeedInitializer(EventDefinitionRepository eventDefinitionRepository) {
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
            new SeedEvent("RAID_ITEM_CREATED", "RAID Item Created", "RAID item created"),
            new SeedEvent("RAID_ITEM_UPDATED", "RAID Item Updated", "RAID item updated"),
            new SeedEvent("RAID_ITEM_RESOLVED", "RAID Item Resolved", "RAID item resolved"),
            new SeedEvent("RAID_ITEM_ESCALATED", "RAID Item Escalated", "RAID item escalated"),
            new SeedEvent("RAID_RISK_CONVERTED_TO_ISSUE", "Risk Converted To Issue", "Risk converted to issue"),
            new SeedEvent("RAID_ACTION_CREATED", "RAID Action Created", "RAID action created"),
            new SeedEvent("RAID_ACTION_COMPLETED", "RAID Action Completed", "RAID action completed"),
            new SeedEvent("DECISION_CREATED", "Decision Created", "Decision created"),
            new SeedEvent("DECISION_DECIDED", "Decision Decided", "Decision decided"),
            new SeedEvent("DECISION_REJECTED", "Decision Rejected", "Decision rejected")
    );
}
