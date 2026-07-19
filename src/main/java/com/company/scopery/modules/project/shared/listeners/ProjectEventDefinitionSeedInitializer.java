package com.company.scopery.modules.project.shared.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.VariableType;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport.VariableSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds known Project/WBS/Task event contracts (sourceSystem = SCOPERY_PROJECT).
 * Emitters/full ownership remain Phase 09/10.
 */
@Component
@Order(15)
public class ProjectEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(ProjectEventDefinitionSeedInitializer.class);
    public static final String SOURCE_SYSTEM = "SCOPERY_PROJECT";
    public static final String OWNER_MODULE = "PROJECT";

    private final EventDefinitionRepository eventDefinitionRepository;

    public ProjectEventDefinitionSeedInitializer(EventDefinitionRepository eventDefinitionRepository) {
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

        EventDefinition taskAssigned = EventDefinitionSeedSupport.findOrCreate(
                eventDefinitionRepository,
                SOURCE_SYSTEM,
                "TASK_ASSIGNED",
                "Task Assigned",
                "Emitted when a task is assigned to a user",
                EventDataClassification.INTERNAL,
                OWNER_MODULE);
        EventDefinitionSeedSupport.ensureVariables(eventDefinitionRepository, log, taskAssigned.id(), List.of(
                VariableSpec.of("task.id", "Task ID", VariableType.UUID, true),
                VariableSpec.of("task.code", "Task Code", VariableType.STRING, true),
                VariableSpec.of("task.title", "Task Title", VariableType.STRING, true),
                VariableSpec.of("task.status", "Task Status", VariableType.STRING, true),
                VariableSpec.of("task.assigneeUserId", "Assignee User ID", VariableType.UUID, false),
                VariableSpec.of("task.dueDate", "Due Date", VariableType.DATE, false),
                VariableSpec.of("occurredAt", "Occurred At", VariableType.DATETIME, true),
                VariableSpec.of("traceId", "Trace ID", VariableType.STRING, true)
        ));

        log.info("[ProjectEventDefinitionSeed] Project event seeding complete ({} events)", EVENTS.size());
    }

    private record SeedEvent(String code, String name, String description) {}

    private static final List<SeedEvent> EVENTS = List.of(
            new SeedEvent("PROJECT_CREATED", "Project Created", "Project created"),
            new SeedEvent("PROJECT_UPDATED", "Project Updated", "Project updated"),
            new SeedEvent("PROJECT_ACTIVATED", "Project Activated", "Project activated"),
            new SeedEvent("PROJECT_ARCHIVED", "Project Archived", "Project archived"),
            new SeedEvent("PROJECT_PHASE_CREATED", "Project Phase Created", "Project phase created"),
            new SeedEvent("PROJECT_PHASE_UPDATED", "Project Phase Updated", "Project phase updated"),
            new SeedEvent("PROJECT_PHASE_ACTIVATED", "Project Phase Activated", "Project phase activated"),
            new SeedEvent("PROJECT_PHASE_COMPLETED", "Project Phase Completed", "Project phase completed"),
            new SeedEvent("PROJECT_PHASE_ARCHIVED", "Project Phase Archived", "Project phase archived"),
            new SeedEvent("WBS_NODE_CREATED", "WBS Node Created", "WBS node created"),
            new SeedEvent("WBS_NODE_UPDATED", "WBS Node Updated", "WBS node updated"),
            new SeedEvent("WBS_NODE_MOVED", "WBS Node Moved", "WBS node moved"),
            new SeedEvent("WBS_NODE_ARCHIVED", "WBS Node Archived", "WBS node archived"),
            new SeedEvent("TASK_CREATED", "Task Created", "Task created"),
            new SeedEvent("TASK_UPDATED", "Task Updated", "Task updated"),
            new SeedEvent("TASK_ASSIGNED", "Task Assigned", "Task assigned"),
            new SeedEvent("TASK_STARTED", "Task Started", "Task started"),
            new SeedEvent("TASK_BLOCKED", "Task Blocked", "Task blocked"),
            new SeedEvent("TASK_COMPLETED", "Task Completed", "Task completed"),
            new SeedEvent("TASK_CANCELLED", "Task Cancelled", "Task cancelled"),
            new SeedEvent("TASK_ARCHIVED", "Task Archived", "Task archived"),
            new SeedEvent("TASK_DEPENDENCY_CREATED", "Task Dependency Created", "Task dependency created"),
            new SeedEvent("TASK_DEPENDENCY_REMOVED", "Task Dependency Removed", "Task dependency removed"),
            // Phase 11 — phase definition + project template (§12.1)
            new SeedEvent("PHASE_DEFINITION_CREATED", "Phase Definition Created", "Phase definition created"),
            new SeedEvent("PHASE_DEFINITION_UPDATED", "Phase Definition Updated", "Phase definition updated"),
            new SeedEvent("PHASE_DEFINITION_ACTIVATED", "Phase Definition Activated", "Phase definition activated"),
            new SeedEvent("PHASE_DEFINITION_DEACTIVATED", "Phase Definition Deactivated", "Phase definition deactivated"),
            new SeedEvent("PHASE_DEFINITION_ARCHIVED", "Phase Definition Archived", "Phase definition archived"),
            new SeedEvent("PROJECT_TEMPLATE_CREATED", "Project Template Created", "Project template created"),
            new SeedEvent("PROJECT_TEMPLATE_UPDATED", "Project Template Updated", "Project template updated"),
            new SeedEvent("PROJECT_TEMPLATE_ACTIVATED", "Project Template Activated", "Project template activated"),
            new SeedEvent("PROJECT_TEMPLATE_DEACTIVATED", "Project Template Deactivated", "Project template deactivated"),
            new SeedEvent("PROJECT_TEMPLATE_ARCHIVED", "Project Template Archived", "Project template archived"),
            new SeedEvent("PROJECT_TEMPLATE_VERSION_CREATED", "Project Template Version Created", "Project template version created"),
            new SeedEvent("PROJECT_TEMPLATE_VERSION_UPDATED", "Project Template Version Updated", "Project template version updated"),
            new SeedEvent("PROJECT_TEMPLATE_VERSION_PUBLISHED", "Project Template Version Published", "Project template version published"),
            new SeedEvent("PROJECT_TEMPLATE_VERSION_ARCHIVED", "Project Template Version Archived", "Project template version archived"),
            new SeedEvent("PROJECT_TEMPLATE_VERSION_DUPLICATED", "Project Template Version Duplicated", "Project template version duplicated"),
            new SeedEvent("PROJECT_TEMPLATE_PHASE_CREATED", "Project Template Phase Created", "Project template phase created"),
            new SeedEvent("PROJECT_TEMPLATE_PHASE_UPDATED", "Project Template Phase Updated", "Project template phase updated"),
            new SeedEvent("PROJECT_TEMPLATE_PHASE_DELETED", "Project Template Phase Deleted", "Project template phase deleted"),
            new SeedEvent("PROJECT_TEMPLATE_PHASES_REORDERED", "Project Template Phases Reordered", "Project template phases reordered"),
            new SeedEvent("PROJECT_TEMPLATE_WBS_NODE_CREATED", "Project Template WBS Node Created", "Project template WBS node created"),
            new SeedEvent("PROJECT_TEMPLATE_WBS_NODE_UPDATED", "Project Template WBS Node Updated", "Project template WBS node updated"),
            new SeedEvent("PROJECT_TEMPLATE_WBS_NODE_MOVED", "Project Template WBS Node Moved", "Project template WBS node moved"),
            new SeedEvent("PROJECT_TEMPLATE_WBS_NODE_DELETED", "Project Template WBS Node Deleted", "Project template WBS node deleted"),
            new SeedEvent("PROJECT_TEMPLATE_TASK_CREATED", "Project Template Task Created", "Project template task created"),
            new SeedEvent("PROJECT_TEMPLATE_TASK_UPDATED", "Project Template Task Updated", "Project template task updated"),
            new SeedEvent("PROJECT_TEMPLATE_TASK_DELETED", "Project Template Task Deleted", "Project template task deleted"),
            new SeedEvent("PROJECT_TEMPLATE_TASK_DEPENDENCY_CREATED", "Project Template Task Dependency Created", "Project template task dependency created"),
            new SeedEvent("PROJECT_TEMPLATE_TASK_DEPENDENCY_REMOVED", "Project Template Task Dependency Removed", "Project template task dependency removed"),
            new SeedEvent("PROJECT_TEMPLATE_APPLIED", "Project Template Applied", "Project template applied to a project")
    );
}
