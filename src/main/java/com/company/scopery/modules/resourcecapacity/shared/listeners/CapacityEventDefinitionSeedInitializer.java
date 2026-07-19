package com.company.scopery.modules.resourcecapacity.shared.listeners;

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

/**
 * Seeds known Resource Capacity event contracts (sourceSystem = SCOPERY_CAPACITY).
 */
@Component
@Order(16)
public class CapacityEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(CapacityEventDefinitionSeedInitializer.class);
    public static final String SOURCE_SYSTEM = "SCOPERY_CAPACITY";
    public static final String OWNER_MODULE = "RESOURCE_CAPACITY";

    private final EventDefinitionRepository eventDefinitionRepository;

    public CapacityEventDefinitionSeedInitializer(EventDefinitionRepository eventDefinitionRepository) {
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

        log.info("[CapacityEventDefinitionSeed] Resource capacity event seeding complete ({} events)", EVENTS.size());
    }

    private record SeedEvent(String code, String name, String description) {}

    private static final List<SeedEvent> EVENTS = List.of(
            new SeedEvent("CAPACITY_CALENDAR_CREATED", "Working Calendar Created", "Working calendar created"),
            new SeedEvent("CAPACITY_CALENDAR_UPDATED", "Working Calendar Updated", "Working calendar updated"),
            new SeedEvent("CAPACITY_CALENDAR_ACTIVATED", "Working Calendar Activated", "Working calendar activated"),
            new SeedEvent("CAPACITY_CALENDAR_DEACTIVATED", "Working Calendar Deactivated", "Working calendar deactivated"),
            new SeedEvent("CAPACITY_CALENDAR_ARCHIVED", "Working Calendar Archived", "Working calendar archived"),
            new SeedEvent("CAPACITY_CALENDAR_DEFAULT_CHANGED", "Working Calendar Default Changed", "Default working calendar changed"),
            new SeedEvent("CAPACITY_DAY_RULES_UPDATED", "Calendar Day Rules Updated", "Calendar day rules replaced"),
            new SeedEvent("CAPACITY_EXCEPTION_CREATED", "Calendar Exception Created", "Calendar exception created"),
            new SeedEvent("CAPACITY_EXCEPTION_UPDATED", "Calendar Exception Updated", "Calendar exception updated"),
            new SeedEvent("CAPACITY_EXCEPTION_DELETED", "Calendar Exception Deleted", "Calendar exception deleted"),
            new SeedEvent("CAPACITY_PROFILE_CREATED", "User Capacity Profile Created", "User capacity profile created"),
            new SeedEvent("CAPACITY_PROFILE_UPDATED", "User Capacity Profile Updated", "User capacity profile updated"),
            new SeedEvent("CAPACITY_PROFILE_ACTIVATED", "User Capacity Profile Activated", "User capacity profile activated"),
            new SeedEvent("CAPACITY_PROFILE_DEACTIVATED", "User Capacity Profile Deactivated", "User capacity profile deactivated"),
            new SeedEvent("CAPACITY_PROFILE_ARCHIVED", "User Capacity Profile Archived", "User capacity profile archived"),
            new SeedEvent("PROJECT_ALLOCATION_CREATED", "Project Resource Allocation Created", "Project resource allocation created"),
            new SeedEvent("PROJECT_ALLOCATION_UPDATED", "Project Resource Allocation Updated", "Project resource allocation updated"),
            new SeedEvent("PROJECT_ALLOCATION_ACTIVATED", "Project Resource Allocation Activated", "Project resource allocation activated"),
            new SeedEvent("PROJECT_ALLOCATION_DEACTIVATED", "Project Resource Allocation Deactivated", "Project resource allocation deactivated"),
            new SeedEvent("PROJECT_ALLOCATION_ARCHIVED", "Project Resource Allocation Archived", "Project resource allocation archived"),
            // Phase 37
            new SeedEvent("RESOURCE_PROFILE_CREATED", "Resource Profile Created", "Resource profile created"),
            new SeedEvent("RESOURCE_PROFILE_SYNCED_FROM_MEMBER", "Resource Profile Synced", "Resource profiles synced from members"),
            new SeedEvent("RESOURCE_ROLE_CREATED", "Resource Role Created", "Resource role created"),
            new SeedEvent("RESOURCE_SKILL_CREATED", "Resource Skill Created", "Resource skill created"),
            new SeedEvent("EFFORT_ESTIMATE_CREATED", "Effort Estimate Created", "Effort estimate created"),
            new SeedEvent("EFFORT_FORECAST_REBUILT", "Effort Forecast Rebuilt", "Effort forecast rebuilt"),
            new SeedEvent("PROJECT_CAPACITY_SUMMARY_REBUILT", "Project Capacity Summary Rebuilt", "Project capacity summary rebuilt"),
            new SeedEvent("RESOURCE_COST_INPUT_REBUILT", "Resource Cost Input Rebuilt", "Resource cost input rebuilt"),
            new SeedEvent("RESOURCE_RISK_FLAG_CREATED", "Resource Risk Flag Created", "Resource risk flag created"),
            new SeedEvent("RESOURCE_RISK_FLAG_MITIGATED", "Resource Risk Flag Mitigated", "Resource risk flag mitigated"),
            new SeedEvent("RESOURCE_UTILIZATION_REBUILT", "Resource Utilization Rebuilt", "Resource utilization rebuilt"),
            new SeedEvent("WORKLOAD_SNAPSHOT_CREATED", "Workload Snapshot Created", "Workload snapshot captured"),
            new SeedEvent("RESOURCE_OVERLOAD_DETECTED", "Resource Overload Detected", "Resource overload threshold breached")
    );
}
