package com.company.scopery.modules.project.shared.support;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.project.gantt.shared.listeners.GanttEventDefinitionSeedInitializer;
import com.company.scopery.modules.project.milestone.domain.model.ProjectMilestone;
import com.company.scopery.modules.project.phasedefinition.domain.model.PhaseDefinition;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.scheduleoverride.domain.model.TaskScheduleOverride;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRun;
import com.company.scopery.modules.project.scheduling.shared.listeners.SchedulingEventDefinitionSeedInitializer;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.TaskSchedule;
import com.company.scopery.modules.project.shared.listeners.ProjectEventDefinitionSeedInitializer;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependency;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersion;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ProjectPlatformPublisher {

    public static final String AGGREGATE_PROJECT = "PROJECT";
    public static final String AGGREGATE_PROJECT_PHASE = "PROJECT_PHASE";
    public static final String AGGREGATE_WBS_NODE = "WBS_NODE";
    public static final String AGGREGATE_TASK = "TASK";
    public static final String AGGREGATE_TASK_DEPENDENCY = "TASK_DEPENDENCY";
    public static final String AGGREGATE_PHASE_DEFINITION = "PHASE_DEFINITION";
    public static final String AGGREGATE_PROJECT_TEMPLATE = "PROJECT_TEMPLATE";
    public static final String AGGREGATE_PROJECT_TEMPLATE_VERSION = "PROJECT_TEMPLATE_VERSION";
    public static final String AGGREGATE_SCHEDULE_RUN = "SCHEDULE_RUN";
    public static final String AGGREGATE_TASK_SCHEDULE = "TASK_SCHEDULE";
    public static final String AGGREGATE_PROJECT_MILESTONE = "PROJECT_MILESTONE";
    public static final String AGGREGATE_TASK_SCHEDULE_OVERRIDE = "TASK_SCHEDULE_OVERRIDE";
    public static final String AGGREGATE_GANTT = "GANTT";

    private final TransactionalOutboxService outboxService;
    private final ImmutableAuditEventService auditEventService;

    public ProjectPlatformPublisher(TransactionalOutboxService outboxService,
                                    ImmutableAuditEventService auditEventService) {
        this.outboxService = outboxService;
        this.auditEventService = auditEventService;
    }

    public void enqueueProject(Project project, String eventCode) {
        outboxService.enqueue(
                AGGREGATE_PROJECT,
                project.id(),
                eventCode,
                ProjectEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                1,
                projectPayload(project));
    }

    public void enqueuePhase(ProjectPhase phase, String eventCode) {
        outboxService.enqueue(
                AGGREGATE_PROJECT_PHASE,
                phase.id(),
                eventCode,
                ProjectEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                1,
                phasePayload(phase));
    }

    public void enqueueWbs(WbsNode node, String eventCode) {
        outboxService.enqueue(
                AGGREGATE_WBS_NODE,
                node.id(),
                eventCode,
                ProjectEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                1,
                wbsPayload(node));
    }

    public void enqueueTask(Task task, String eventCode) {
        outboxService.enqueue(
                AGGREGATE_TASK,
                task.id(),
                eventCode,
                ProjectEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                1,
                taskPayload(task));
    }

    public void enqueueDependency(TaskDependency dep, String eventCode) {
        outboxService.enqueue(
                AGGREGATE_TASK_DEPENDENCY,
                dep.id(),
                eventCode,
                ProjectEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                1,
                dependencyPayload(dep));
    }

    public void enqueuePhaseDefinition(PhaseDefinition pd, String eventCode) {
        outboxService.enqueue(
                AGGREGATE_PHASE_DEFINITION,
                pd.id(),
                eventCode,
                ProjectEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                1,
                phaseDefinitionPayload(pd));
    }

    public void enqueueTemplate(ProjectTemplate template, String eventCode) {
        outboxService.enqueue(
                AGGREGATE_PROJECT_TEMPLATE,
                template.id(),
                eventCode,
                ProjectEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                1,
                templatePayload(template));
    }

    public void enqueueTemplateVersion(ProjectTemplateVersion version, String eventCode) {
        outboxService.enqueue(
                AGGREGATE_PROJECT_TEMPLATE_VERSION,
                version.id(),
                eventCode,
                ProjectEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                1,
                templateVersionPayload(version));
    }

    public void enqueueScheduleRun(ScheduleRun run, String eventCode) {
        outboxService.enqueue(AGGREGATE_SCHEDULE_RUN, run.id(), eventCode,
                SchedulingEventDefinitionSeedInitializer.SOURCE_SYSTEM, 1, scheduleRunPayload(run));
    }

    public void enqueueTaskSchedule(TaskSchedule schedule, String eventCode) {
        outboxService.enqueue(AGGREGATE_TASK_SCHEDULE, schedule.id(), eventCode,
                SchedulingEventDefinitionSeedInitializer.SOURCE_SYSTEM, 1, taskSchedulePayload(schedule));
    }

    public void auditScheduleRecalculation(UUID actorId, Project project, ScheduleRun run) {
        auditEventService.record(AuditEventType.SCHEDULE_RECALCULATION, actorId, "USER",
                AGGREGATE_SCHEDULE_RUN, run.id(), project.organizationId(), project.workspaceId(),
                null, scheduleRunPayload(run), "Project schedule recalculated");
    }

    public void auditScheduleMarkedCurrent(UUID actorId, Project project, ScheduleRun run) {
        auditEventService.record(AuditEventType.SCHEDULE_RUN_MARKED_CURRENT, actorId, "USER",
                AGGREGATE_SCHEDULE_RUN, run.id(), project.organizationId(), project.workspaceId(),
                null, scheduleRunPayload(run), "Schedule run marked current");
    }

    public void auditScheduleCancelled(UUID actorId, Project project, ScheduleRun run) {
        auditEventService.record(AuditEventType.SCHEDULE_RUN_CANCELLED, actorId, "USER",
                AGGREGATE_SCHEDULE_RUN, run.id(), project.organizationId(), project.workspaceId(),
                null, scheduleRunPayload(run), "Schedule run cancelled");
    }

    public void enqueueMilestone(ProjectMilestone milestone, String eventCode) {
        outboxService.enqueue(AGGREGATE_PROJECT_MILESTONE, milestone.id(), eventCode,
                GanttEventDefinitionSeedInitializer.SOURCE_SYSTEM, 1, milestonePayload(milestone));
    }

    public void enqueueOverride(TaskScheduleOverride override, String eventCode) {
        outboxService.enqueue(AGGREGATE_TASK_SCHEDULE_OVERRIDE, override.id(), eventCode,
                GanttEventDefinitionSeedInitializer.SOURCE_SYSTEM, 1, overridePayload(override));
    }

    public void enqueueGantt(UUID projectId, String eventCode, Map<String, Object> payload) {
        outboxService.enqueue(AGGREGATE_GANTT, projectId, eventCode,
                GanttEventDefinitionSeedInitializer.SOURCE_SYSTEM, 1, payload);
    }

    public void auditGanttTaskMoved(UUID actorId, Project project, TaskScheduleOverride override) {
        auditEventService.record(AuditEventType.GANTT_TASK_MOVED, actorId, "USER",
                AGGREGATE_TASK_SCHEDULE_OVERRIDE, override.id(), project.organizationId(), project.workspaceId(),
                null, overridePayload(override), "Gantt task moved");
    }

    public void auditGanttTaskResized(UUID actorId, Project project, TaskScheduleOverride override) {
        auditEventService.record(AuditEventType.GANTT_TASK_RESIZED, actorId, "USER",
                AGGREGATE_TASK_SCHEDULE_OVERRIDE, override.id(), project.organizationId(), project.workspaceId(),
                null, overridePayload(override), "Gantt task resized");
    }

    public void auditGanttOverrideCleared(UUID actorId, Project project, TaskScheduleOverride override) {
        auditEventService.record(AuditEventType.GANTT_TASK_OVERRIDE_CLEARED, actorId, "USER",
                AGGREGATE_TASK_SCHEDULE_OVERRIDE, override.id(), project.organizationId(), project.workspaceId(),
                null, overridePayload(override), "Gantt task override cleared");
    }

    public void auditMilestoneAchieved(UUID actorId, Project project, ProjectMilestone milestone) {
        auditEventService.record(AuditEventType.PROJECT_MILESTONE_ACHIEVED, actorId, "USER",
                AGGREGATE_PROJECT_MILESTONE, milestone.id(), project.organizationId(), project.workspaceId(),
                null, milestonePayload(milestone), "Project milestone achieved");
    }

    public void auditMilestoneArchived(UUID actorId, Project project, ProjectMilestone milestone) {
        auditEventService.record(AuditEventType.PROJECT_MILESTONE_ARCHIVED, actorId, "USER",
                AGGREGATE_PROJECT_MILESTONE, milestone.id(), project.organizationId(), project.workspaceId(),
                null, milestonePayload(milestone), "Project milestone archived");
    }

    public void enqueueTemplateChild(String aggregateType, UUID aggregateId, String eventCode,
                                     Map<String, Object> payload) {
        outboxService.enqueue(
                aggregateType,
                aggregateId,
                eventCode,
                ProjectEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                1,
                payload);
    }

    public void auditProjectArchived(UUID actorId, Project project) {
        auditEventService.record(
                AuditEventType.PROJECT_ARCHIVED,
                actorId,
                "USER",
                AGGREGATE_PROJECT,
                project.id(),
                project.organizationId(),
                project.workspaceId(),
                null,
                projectPayload(project),
                "Project archived");
    }

    public void auditPhaseArchived(UUID actorId, ProjectPhase phase, UUID organizationId, UUID workspaceId) {
        auditEventService.record(
                AuditEventType.PROJECT_PHASE_ARCHIVED,
                actorId,
                "USER",
                AGGREGATE_PROJECT_PHASE,
                phase.id(),
                organizationId,
                workspaceId,
                null,
                phasePayload(phase),
                "Project phase archived");
    }

    public void auditWbsArchived(UUID actorId, WbsNode node, UUID organizationId, UUID workspaceId) {
        auditEventService.record(
                AuditEventType.WBS_NODE_ARCHIVED,
                actorId,
                "USER",
                AGGREGATE_WBS_NODE,
                node.id(),
                organizationId,
                workspaceId,
                null,
                wbsPayload(node),
                "WBS node archived");
    }

    public void auditTaskArchived(UUID actorId, Task task, UUID organizationId, UUID workspaceId) {
        auditEventService.record(
                AuditEventType.TASK_ARCHIVED,
                actorId,
                "USER",
                AGGREGATE_TASK,
                task.id(),
                organizationId,
                workspaceId,
                null,
                taskPayload(task),
                "Task archived");
    }

    public void auditDependencyRemoved(UUID actorId, TaskDependency dep, UUID organizationId, UUID workspaceId) {
        auditEventService.record(
                AuditEventType.TASK_DEPENDENCY_REMOVED,
                actorId,
                "USER",
                AGGREGATE_TASK_DEPENDENCY,
                dep.id(),
                organizationId,
                workspaceId,
                null,
                dependencyPayload(dep),
                "Task dependency removed");
    }

    public void auditTaskPlanningChanged(UUID actorId, Task before, Task after,
                                         UUID organizationId, UUID workspaceId, String reason) {
        Map<String, Object> beforeMap = new LinkedHashMap<>();
        beforeMap.put("inChargeUserId", before.inChargeUserId());
        beforeMap.put("estimateHours", before.estimateHours());
        beforeMap.put("dueDate", before.dueDate() != null ? before.dueDate().toString() : null);

        Map<String, Object> afterMap = new LinkedHashMap<>();
        afterMap.put("inChargeUserId", after.inChargeUserId());
        afterMap.put("estimateHours", after.estimateHours());
        afterMap.put("dueDate", after.dueDate() != null ? after.dueDate().toString() : null);

        auditEventService.record(
                AuditEventType.TASK_PLANNING_CHANGED,
                actorId,
                "USER",
                AGGREGATE_TASK,
                after.id(),
                organizationId,
                workspaceId,
                beforeMap,
                afterMap,
                reason);
    }

    public void auditPhaseDefinitionArchived(UUID actorId, PhaseDefinition pd) {
        auditEventService.record(
                AuditEventType.PHASE_DEFINITION_ARCHIVED,
                actorId,
                "USER",
                AGGREGATE_PHASE_DEFINITION,
                pd.id(),
                pd.organizationId(),
                pd.workspaceId(),
                null,
                phaseDefinitionPayload(pd),
                "Phase definition archived");
    }

    public void auditTemplateArchived(UUID actorId, ProjectTemplate template) {
        auditEventService.record(
                AuditEventType.PROJECT_TEMPLATE_ARCHIVED,
                actorId,
                "USER",
                AGGREGATE_PROJECT_TEMPLATE,
                template.id(),
                template.organizationId(),
                template.workspaceId(),
                null,
                templatePayload(template),
                "Project template archived");
    }

    public void auditTemplateVersionPublished(UUID actorId, ProjectTemplateVersion version, ProjectTemplate template) {
        auditEventService.record(
                AuditEventType.PROJECT_TEMPLATE_VERSION_PUBLISHED,
                actorId,
                "USER",
                AGGREGATE_PROJECT_TEMPLATE_VERSION,
                version.id(),
                template.organizationId(),
                template.workspaceId(),
                null,
                templateVersionPayload(version),
                "Project template version published");
    }

    public void auditTemplateApplied(UUID actorId, Project project, ProjectTemplate template, ProjectTemplateVersion version) {
        Map<String, Object> payload = projectPayload(project);
        payload.put("sourceTemplateId", template.id());
        payload.put("sourceTemplateVersionId", version.id());
        auditEventService.record(
                AuditEventType.PROJECT_TEMPLATE_APPLIED,
                actorId,
                "USER",
                AGGREGATE_PROJECT,
                project.id(),
                project.organizationId(),
                project.workspaceId(),
                null,
                payload,
                "Project template applied");
    }

    private Map<String, Object> projectPayload(Project p) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", p.id());
        map.put("workspaceId", p.workspaceId());
        map.put("organizationId", p.organizationId());
        map.put("code", p.code());
        map.put("name", p.name());
        map.put("status", p.status().name());
        map.put("ownerUserId", p.ownerUserId());
        map.put("sourceTemplateId", p.sourceTemplateId());
        map.put("sourceTemplateVersionId", p.sourceTemplateVersionId());
        return map;
    }

    private Map<String, Object> scheduleRunPayload(ScheduleRun run) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("scheduleRunId", run.id());
        map.put("projectId", run.projectId());
        map.put("workspaceId", run.workspaceId());
        map.put("status", run.status().name());
        map.put("algorithmVersion", run.algorithmVersion());
        map.put("planningStartDate", run.planningStartDate().toString());
        map.put("planningEndDate", run.planningEndDate().toString());
        return map;
    }

    private Map<String, Object> taskSchedulePayload(TaskSchedule schedule) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("taskScheduleId", schedule.id());
        map.put("scheduleRunId", schedule.scheduleRunId());
        map.put("projectId", schedule.projectId());
        map.put("taskId", schedule.taskId());
        map.put("assigneeUserId", schedule.assigneeUserId());
        map.put("estimatedStartDate", schedule.estimatedStartDate() != null ? schedule.estimatedStartDate().toString() : null);
        map.put("estimatedFinishDate", schedule.estimatedFinishDate() != null ? schedule.estimatedFinishDate().toString() : null);
        map.put("dueDate", schedule.dueDate() != null ? schedule.dueDate().toString() : null);
        map.put("dueDateCapacityGapHours", schedule.dueDateCapacityGapHours());
        map.put("riskStatus", schedule.riskStatus().name());
        map.put("scheduleStatus", schedule.scheduleStatus().name());
        return map;
    }

    private Map<String, Object> phasePayload(ProjectPhase p) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", p.id());
        map.put("projectId", p.projectId());
        map.put("code", p.code());
        map.put("name", p.name());
        map.put("status", p.status().name());
        return map;
    }

    private Map<String, Object> wbsPayload(WbsNode n) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", n.id());
        map.put("projectId", n.projectId());
        map.put("code", n.code());
        map.put("title", n.title());
        map.put("status", n.status().name());
        map.put("parentId", n.parentId());
        return map;
    }

    private Map<String, Object> taskPayload(Task t) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", t.id());
        map.put("projectId", t.projectId());
        map.put("code", t.code());
        map.put("title", t.title());
        map.put("status", t.status().name());
        map.put("estimateHours", t.estimateHours());
        map.put("dueDate", t.dueDate() != null ? t.dueDate().toString() : null);
        map.put("inChargeUserId", t.inChargeUserId());
        map.put("wbsNodeId", t.wbsNodeId());
        map.put("projectPhaseId", t.projectPhaseId());
        return map;
    }

    private Map<String, Object> dependencyPayload(TaskDependency d) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", d.id());
        map.put("projectId", d.projectId());
        map.put("predecessorTaskId", d.predecessorTaskId());
        map.put("successorTaskId", d.successorTaskId());
        map.put("dependencyType", d.dependencyType().name());
        map.put("status", d.status().name());
        return map;
    }

    private Map<String, Object> phaseDefinitionPayload(PhaseDefinition pd) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", pd.id());
        map.put("scope", pd.scope() != null ? pd.scope().name() : null);
        map.put("organizationId", pd.organizationId());
        map.put("workspaceId", pd.workspaceId());
        map.put("code", pd.code());
        map.put("name", pd.name());
        map.put("status", pd.status() != null ? pd.status().name() : null);
        map.put("isSystemDefault", pd.isSystemDefault());
        return map;
    }

    private Map<String, Object> templatePayload(ProjectTemplate t) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", t.id());
        map.put("code", t.code());
        map.put("name", t.name());
        map.put("scope", t.scope() != null ? t.scope().name() : null);
        map.put("organizationId", t.organizationId());
        map.put("workspaceId", t.workspaceId());
        map.put("status", t.status() != null ? t.status().name() : null);
        map.put("currentVersionId", t.currentVersionId());
        return map;
    }

    private Map<String, Object> templateVersionPayload(ProjectTemplateVersion v) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", v.id());
        map.put("projectTemplateId", v.projectTemplateId());
        map.put("versionNumber", v.versionNumber());
        map.put("name", v.name());
        map.put("status", v.status() != null ? v.status().name() : null);
        map.put("publishedAt", v.publishedAt() != null ? v.publishedAt().toString() : null);
        return map;
    }

    private Map<String, Object> milestonePayload(ProjectMilestone m) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", m.id());
        map.put("projectId", m.projectId());
        map.put("projectPhaseId", m.projectPhaseId());
        map.put("wbsNodeId", m.wbsNodeId());
        map.put("code", m.code());
        map.put("name", m.name());
        map.put("milestoneDate", m.milestoneDate() != null ? m.milestoneDate().toString() : null);
        map.put("status", m.status().name());
        return map;
    }

    private Map<String, Object> overridePayload(TaskScheduleOverride o) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", o.id());
        map.put("projectId", o.projectId());
        map.put("taskId", o.taskId());
        map.put("overrideType", o.overrideType().name());
        map.put("manualStartDate", o.manualStartDate() != null ? o.manualStartDate().toString() : null);
        map.put("manualFinishDate", o.manualFinishDate() != null ? o.manualFinishDate().toString() : null);
        map.put("manualDueDate", o.manualDueDate() != null ? o.manualDueDate().toString() : null);
        map.put("status", o.status().name());
        map.put("reason", o.reason());
        return map;
    }
}
