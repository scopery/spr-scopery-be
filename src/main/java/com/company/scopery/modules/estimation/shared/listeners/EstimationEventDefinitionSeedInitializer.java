package com.company.scopery.modules.estimation.shared.listeners;

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
@Order(22)
public class EstimationEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    public static final String SOURCE_SYSTEM = "SCOPERY_ESTIMATION";
    public static final String OWNER_MODULE = "ESTIMATION";

    private final EventDefinitionRepository eventDefinitionRepository;

    public EstimationEventDefinitionSeedInitializer(EventDefinitionRepository eventDefinitionRepository) {
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
            new SeedEvent("ESTIMATION_RUN_CREATED", "Estimation Run Created", "An estimation run was created"),
            new SeedEvent("ESTIMATION_RUN_STARTED", "Estimation Run Started", "An estimation run started"),
            new SeedEvent("ESTIMATION_RUN_COMPLETED", "Estimation Run Completed", "An estimation run completed"),
            new SeedEvent("ESTIMATION_RUN_FAILED", "Estimation Run Failed", "An estimation run failed"),
            new SeedEvent("ESTIMATION_RUN_CANCELLED", "Estimation Run Cancelled", "An estimation run was cancelled"),
            new SeedEvent("ESTIMATION_RUN_MARKED_CURRENT", "Estimation Run Marked Current", "An estimation run was marked current"),
            new SeedEvent("TASK_ESTIMATE_SNAPSHOT_CREATED", "Task Estimate Snapshot Created", "A task estimate snapshot was created"),
            new SeedEvent("TASK_ESTIMATE_RATE_UNRESOLVED", "Task Estimate Rate Unresolved", "Task rate could not be resolved"),
            new SeedEvent("TASK_ESTIMATE_ROLE_UNRESOLVED", "Task Estimate Role Unresolved", "Task cost role could not be resolved"),
            new SeedEvent("WBS_ESTIMATE_ROLLED_UP", "WBS Estimate Rolled Up", "WBS estimate roll-up was calculated"),
            new SeedEvent("PHASE_ESTIMATE_ROLLED_UP", "Phase Estimate Rolled Up", "Phase estimate roll-up was calculated"),
            new SeedEvent("PROJECT_ESTIMATE_SUMMARY_CREATED", "Project Estimate Summary Created", "Project estimate summary was created"),
            new SeedEvent("ESTIMATION_RATE_SNAPSHOT_USED", "Estimation Rate Snapshot Used", "Rate snapshot was used in estimation"),
            new SeedEvent("ESTIMATION_WARNING_CREATED", "Estimation Warning Created", "An estimation warning was created")
    );
}
