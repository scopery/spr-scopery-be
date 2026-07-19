package com.company.scopery.modules.projectnotification.shared.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.VariableType;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport.VariableSpec;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationEventCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(26)
public class ProjectNotificationEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(ProjectNotificationEventDefinitionSeedInitializer.class);
    public static final String SOURCE_SYSTEM = "SCOPERY_PROJECT";
    public static final String OWNER_MODULE = "PROJECT_NOTIFICATION";

    private final EventDefinitionRepository eventDefinitionRepository;

    public ProjectNotificationEventDefinitionSeedInitializer(EventDefinitionRepository eventDefinitionRepository) {
        this.eventDefinitionRepository = eventDefinitionRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        seed(ProjectNotificationEventCodes.PROJECT_TASK_DUE_SOON, "Project Task Due Soon", "Task is due tomorrow");
        seed(ProjectNotificationEventCodes.PROJECT_TASK_OVERDUE, "Project Task Overdue", "Task is overdue");
        seed(ProjectNotificationEventCodes.PROJECT_TASK_AT_RISK, "Project Task At Risk", "Task schedule is at risk");
        log.info("[ProjectNotificationEventSeed] Reminder/risk event seeding complete");
    }

    private void seed(String code, String name, String description) {
        EventDefinition def = EventDefinitionSeedSupport.findOrCreate(
                eventDefinitionRepository, SOURCE_SYSTEM, code, name, description,
                EventDataClassification.INTERNAL, OWNER_MODULE);
        EventDefinitionSeedSupport.ensureVariables(eventDefinitionRepository, log, def.id(), List.of(
                VariableSpec.of("actor.userId", "Actor User ID", VariableType.UUID, false),
                VariableSpec.of("organization.id", "Organization ID", VariableType.UUID, false),
                VariableSpec.of("workspace.id", "Workspace ID", VariableType.UUID, true),
                VariableSpec.of("project.id", "Project ID", VariableType.UUID, true),
                VariableSpec.of("project.name", "Project Name", VariableType.STRING, false),
                VariableSpec.of("task.id", "Task ID", VariableType.UUID, true),
                VariableSpec.of("task.title", "Task Title", VariableType.STRING, false),
                VariableSpec.of("assignee.userId", "Assignee User ID", VariableType.UUID, false),
                VariableSpec.of("targetUser.userId", "Target User ID", VariableType.UUID, false),
                VariableSpec.of("targetUser.email", "Target User Email", VariableType.STRING, false),
                VariableSpec.of("dueDate", "Due Date", VariableType.DATE, false),
                VariableSpec.of("riskStatus", "Risk Status", VariableType.STRING, false),
                VariableSpec.of("occurredAt", "Occurred At", VariableType.DATETIME, false),
                VariableSpec.of("traceId", "Trace ID", VariableType.STRING, false)
        ));
    }
}
