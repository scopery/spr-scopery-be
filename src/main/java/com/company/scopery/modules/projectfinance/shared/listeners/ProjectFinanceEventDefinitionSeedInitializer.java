package com.company.scopery.modules.projectfinance.shared.listeners;

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
@Order(23)
public class ProjectFinanceEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    public static final String SOURCE_SYSTEM = "SCOPERY_PROJECT_FINANCE";
    public static final String OWNER_MODULE = "PROJECT_FINANCE";

    private final EventDefinitionRepository eventDefinitionRepository;

    public ProjectFinanceEventDefinitionSeedInitializer(EventDefinitionRepository eventDefinitionRepository) {
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
            new SeedEvent("PROJECT_FINANCE_SCENARIO_CREATED", "Finance Scenario Created", "A project finance scenario was created"),
            new SeedEvent("PROJECT_FINANCE_SCENARIO_UPDATED", "Finance Scenario Updated", "A project finance scenario was updated"),
            new SeedEvent("PROJECT_FINANCE_SCENARIO_RECALCULATED", "Finance Scenario Recalculated", "A project finance scenario was recalculated"),
            new SeedEvent("PROJECT_FINANCE_SCENARIO_APPROVED", "Finance Scenario Approved", "A project finance scenario was approved"),
            new SeedEvent("PROJECT_FINANCE_SCENARIO_MARKED_CURRENT", "Finance Scenario Marked Current", "A project finance scenario was marked current"),
            new SeedEvent("PROJECT_FINANCE_SCENARIO_ARCHIVED", "Finance Scenario Archived", "A project finance scenario was archived"),
            new SeedEvent("PROJECT_FINANCE_SCENARIO_DUPLICATED", "Finance Scenario Duplicated", "A project finance scenario was duplicated"),
            new SeedEvent("PROJECT_PHASE_FINANCE_CALCULATED", "Phase Finance Calculated", "Phase finance totals were calculated"),
            new SeedEvent("PROJECT_FINANCE_SUMMARY_CALCULATED", "Finance Summary Calculated", "Project finance summary was calculated"),
            new SeedEvent("PROJECT_CUSTOM_COST_CREATED", "Custom Cost Created", "A planned custom cost was created"),
            new SeedEvent("PROJECT_CUSTOM_COST_UPDATED", "Custom Cost Updated", "A planned custom cost was updated"),
            new SeedEvent("PROJECT_CUSTOM_COST_ARCHIVED", "Custom Cost Archived", "A planned custom cost was archived"),
            new SeedEvent("PROJECT_VENDOR_COST_CREATED", "Vendor Cost Created", "A planned vendor cost was created"),
            new SeedEvent("PROJECT_VENDOR_COST_UPDATED", "Vendor Cost Updated", "A planned vendor cost was updated"),
            new SeedEvent("PROJECT_VENDOR_COST_ARCHIVED", "Vendor Cost Archived", "A planned vendor cost was archived"),
            new SeedEvent("PROJECT_REVENUE_SPLIT_UPDATED", "Revenue Split Updated", "Planned revenue split was updated"),
            new SeedEvent("PROJECT_OVERHEAD_POLICY_UPDATED", "Overhead Policy Updated", "Overhead policy was updated"),
            new SeedEvent("PROJECT_CONTINGENCY_UPDATED", "Contingency Updated", "Contingency policy was updated"),
            new SeedEvent("PROJECT_MARGIN_THRESHOLD_WARNING", "Margin Threshold Warning", "Margin threshold warning was raised")
    );
}
