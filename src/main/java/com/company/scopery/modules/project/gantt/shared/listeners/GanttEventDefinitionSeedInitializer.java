package com.company.scopery.modules.project.gantt.shared.listeners;

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
@Order(18)
public class GanttEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    public static final String SOURCE_SYSTEM = "SCOPERY_GANTT";
    public static final String OWNER_MODULE = "GANTT";

    private final EventDefinitionRepository repository;

    public GanttEventDefinitionSeedInitializer(EventDefinitionRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        EVENTS.forEach(code -> EventDefinitionSeedSupport.findOrCreate(
                repository,
                SOURCE_SYSTEM,
                code,
                title(code),
                title(code) + " event",
                EventDataClassification.INTERNAL,
                OWNER_MODULE));
    }

    private static String title(String code) {
        String s = code.toLowerCase().replace('_', ' ');
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static final List<String> EVENTS = List.of(
            "GANTT_VIEW_GENERATED",
            "GANTT_RECALCULATION_REQUESTED",
            "GANTT_RECALCULATED",
            "GANTT_TASK_MOVED",
            "GANTT_TASK_RESIZED",
            "GANTT_TASK_OVERRIDE_CLEARED",
            "GANTT_DEPENDENCY_CREATED",
            "GANTT_DEPENDENCY_REMOVED",
            "PROJECT_MILESTONE_CREATED",
            "PROJECT_MILESTONE_UPDATED",
            "PROJECT_MILESTONE_ACHIEVED",
            "PROJECT_MILESTONE_ARCHIVED",
            "GANTT_ISSUE_MARKER_CREATED"
    );
}
