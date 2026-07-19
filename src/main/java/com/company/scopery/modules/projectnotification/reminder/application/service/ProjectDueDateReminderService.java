package com.company.scopery.modules.projectnotification.reminder.application.service;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPublisher;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.projectnotification.reminder.domain.enums.ReminderRunType;
import com.company.scopery.modules.projectnotification.reminder.domain.enums.ReminderType;
import com.company.scopery.modules.projectnotification.reminder.domain.model.ProjectReminderEmission;
import com.company.scopery.modules.projectnotification.reminder.domain.model.ProjectReminderEmissionRepository;
import com.company.scopery.modules.projectnotification.reminder.domain.model.ProjectReminderRun;
import com.company.scopery.modules.projectnotification.reminder.domain.model.ProjectReminderRunRepository;
import com.company.scopery.modules.projectnotification.shared.activity.ProjectNotificationActivityLogger;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationActivityActions;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationEntityTypes;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationEventCodes;
import com.company.scopery.modules.projectnotification.shared.error.ProjectNotificationExceptions;
import com.company.scopery.modules.projectnotification.tasksubscription.domain.model.TaskNotificationSubscription;
import com.company.scopery.modules.projectnotification.tasksubscription.domain.model.TaskNotificationSubscriptionRepository;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class ProjectDueDateReminderService {

    private static final int BATCH_LIMIT = 500;
    private static final Set<TaskStatus> EXCLUDED = EnumSet.of(
            TaskStatus.DONE, TaskStatus.CANCELLED, TaskStatus.ARCHIVED);

    private final ProjectReminderRunRepository runs;
    private final ProjectReminderEmissionRepository emissions;
    private final TaskRepository tasks;
    private final ProjectRepository projects;
    private final TaskNotificationSubscriptionRepository taskSubscriptions;
    private final IamUserRepository users;
    private final EventDefinitionRepository eventDefinitions;
    private final EmailNotificationTriggerPublisher notificationPublisher;
    private final ProjectNotificationActivityLogger activityLogger;

    public ProjectDueDateReminderService(
            ProjectReminderRunRepository runs,
            ProjectReminderEmissionRepository emissions,
            TaskRepository tasks,
            ProjectRepository projects,
            TaskNotificationSubscriptionRepository taskSubscriptions,
            IamUserRepository users,
            EventDefinitionRepository eventDefinitions,
            EmailNotificationTriggerPublisher notificationPublisher,
            ProjectNotificationActivityLogger activityLogger) {
        this.runs = runs;
        this.emissions = emissions;
        this.tasks = tasks;
        this.projects = projects;
        this.taskSubscriptions = taskSubscriptions;
        this.users = users;
        this.eventDefinitions = eventDefinitions;
        this.notificationPublisher = notificationPublisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProjectReminderRun runDaily(UUID workspaceId) {
        if (runs.existsByStatus(com.company.scopery.modules.projectnotification.reminder.domain.enums.ReminderRunStatus.RUNNING)) {
            throw ProjectNotificationExceptions.reminderAlreadyRunning();
        }
        ProjectReminderRun run = ProjectReminderRun.start(workspaceId, ReminderRunType.COMBINED_DAILY, MDC.get("traceId"));
        run = runs.save(run);
        activityLogger.logSuccess(ProjectNotificationEntityTypes.REMINDER_RUN, run.id(),
                ProjectNotificationActivityActions.REMINDER_RUN_STARTED, "Reminder run started");
        try {
            LocalDate today = LocalDate.now();
            LocalDate dueSoonDate = today.plusDays(1);
            int dueSoon = processDue(run, dueSoonDate, ReminderType.TASK_DUE_SOON,
                    ProjectNotificationEventCodes.PROJECT_TASK_DUE_SOON, false);
            int overdue = processDue(run, today, ReminderType.TASK_OVERDUE,
                    ProjectNotificationEventCodes.PROJECT_TASK_OVERDUE, true);
            String summary = "{\"dueSoonEmitted\":" + dueSoon + ",\"overdueEmitted\":" + overdue + "}";
            run = runs.save(run.complete(summary));
            activityLogger.logSuccess(ProjectNotificationEntityTypes.REMINDER_RUN, run.id(),
                    ProjectNotificationActivityActions.REMINDER_RUN_COMPLETED, summary);
            return run;
        } catch (RuntimeException ex) {
            run = runs.save(run.fail("PROJECT_REMINDER_RUN_FAILED", ex.getMessage()));
            activityLogger.logSuccess(ProjectNotificationEntityTypes.REMINDER_RUN, run.id(),
                    ProjectNotificationActivityActions.REMINDER_RUN_FAILED, ex.getMessage());
            throw ex;
        }
    }

    private int processDue(ProjectReminderRun run, LocalDate targetDate, ReminderType type,
                           String eventCode, boolean overdueMode) {
        List<Task> candidates = overdueMode
                ? tasks.findOverdueReminderCandidates(targetDate, EXCLUDED, BATCH_LIMIT)
                : tasks.findDueSoonReminderCandidates(targetDate, EXCLUDED, BATCH_LIMIT);
        int emitted = 0;
        Optional<EventDefinition> eventDef = eventDefinitions.findByCode(EventDefinitionCode.of(eventCode));
        if (eventDef.isEmpty()) {
            return 0;
        }
        for (Task task : candidates) {
            Optional<Project> projectOpt = projects.findById(task.projectId());
            if (projectOpt.isEmpty()) continue;
            Project project = projectOpt.get();
            if (project.status() == ProjectStatus.ARCHIVED) continue;
            if (run.workspaceId() != null && !run.workspaceId().equals(project.workspaceId())) continue;

            Set<UUID> recipients = new LinkedHashSet<>();
            if (task.inChargeUserId() != null) recipients.add(task.inChargeUserId());
            taskSubscriptions.findActiveByTaskId(task.id()).stream()
                    .map(TaskNotificationSubscription::subscriberUserId)
                    .forEach(recipients::add);

            for (UUID recipientUserId : recipients) {
                var user = users.findById(recipientUserId);
                if (user.isEmpty() || user.get().status() != IamUserStatus.ACTIVE) continue;
                String dedup = ProjectReminderEmission.buildDedupKey(task.id(), type, targetDate, recipientUserId);
                if (emissions.existsByDedupKey(dedup)) continue;

                Map<String, Object> payload = new HashMap<>();
                payload.put("eventCode", eventCode);
                payload.put("aggregateId", task.id().toString());
                payload.put("occurrenceId", dedup);
                payload.put("project", Map.of("id", project.id().toString(), "name", project.name() == null ? "" : project.name()));
                payload.put("task", Map.of("id", task.id().toString(), "title", task.title() == null ? "" : task.title()));
                payload.put("workspace", Map.of("id", project.workspaceId().toString()));
                payload.put("assignee", Map.of("userId", recipientUserId.toString()));
                payload.put("targetUser", Map.of(
                        "userId", recipientUserId.toString(),
                        "email", user.get().email().value()));
                payload.put("dueDate", targetDate.toString());

                notificationPublisher.publish(new EmailNotificationTriggerPayload(
                        eventDef.get().id(), "SCOPERY_PROJECT", eventCode,
                        project.workspaceId(), null, payload));

                emissions.save(ProjectReminderEmission.emitted(
                        run.id(), project.id(), task.id(), recipientUserId, type, targetDate, dedup));
                emitted++;
            }
        }
        return emitted;
    }
}
