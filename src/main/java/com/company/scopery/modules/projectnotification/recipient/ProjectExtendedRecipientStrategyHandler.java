package com.company.scopery.modules.projectnotification.recipient;

import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.notification.emailrule.application.service.EmailRecipientResolver;
import com.company.scopery.modules.notification.emailrule.application.service.ExtendedRecipientStrategyHandler;
import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRecipientStrategy;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRule;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.projectnotification.preference.domain.enums.PreferenceChannel;
import com.company.scopery.modules.projectnotification.preference.domain.model.ProjectNotificationPreference;
import com.company.scopery.modules.projectnotification.preference.domain.model.ProjectNotificationPreferenceRepository;
import com.company.scopery.modules.projectnotification.subscription.domain.enums.ProjectSubscriptionType;
import com.company.scopery.modules.projectnotification.subscription.domain.model.ProjectNotificationSubscription;
import com.company.scopery.modules.projectnotification.subscription.domain.model.ProjectNotificationSubscriptionRepository;
import com.company.scopery.modules.projectnotification.tasksubscription.domain.model.TaskNotificationSubscription;
import com.company.scopery.modules.projectnotification.tasksubscription.domain.model.TaskNotificationSubscriptionRepository;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProjectExtendedRecipientStrategyHandler implements ExtendedRecipientStrategyHandler {

    private static final Set<EmailRecipientStrategy> SUPPORTED = EnumSet.of(
            EmailRecipientStrategy.TASK_ASSIGNEE,
            EmailRecipientStrategy.TASK_IN_CHARGE,
            EmailRecipientStrategy.TASK_WATCHERS,
            EmailRecipientStrategy.PROJECT_WATCHERS,
            EmailRecipientStrategy.PROJECT_OWNER,
            EmailRecipientStrategy.PROJECT_MANAGER,
            EmailRecipientStrategy.CHANGE_REQUEST_REQUESTER,
            EmailRecipientStrategy.CHANGE_WATCHERS,
            EmailRecipientStrategy.QUOTE_WATCHERS,
            EmailRecipientStrategy.FINANCE_WATCHERS,
            EmailRecipientStrategy.BASELINE_WATCHERS,
            EmailRecipientStrategy.EVENT_VARIABLE_USER
    );

    private final ProjectRepository projects;
    private final TaskRepository tasks;
    private final ProjectNotificationSubscriptionRepository projectSubscriptions;
    private final TaskNotificationSubscriptionRepository taskSubscriptions;
    private final ProjectNotificationPreferenceRepository preferences;
    private final IamUserRepository users;
    private final WorkspaceMemberRepository members;

    public ProjectExtendedRecipientStrategyHandler(
            ProjectRepository projects, TaskRepository tasks,
            ProjectNotificationSubscriptionRepository projectSubscriptions,
            TaskNotificationSubscriptionRepository taskSubscriptions,
            ProjectNotificationPreferenceRepository preferences,
            IamUserRepository users, WorkspaceMemberRepository members) {
        this.projects = projects;
        this.tasks = tasks;
        this.projectSubscriptions = projectSubscriptions;
        this.taskSubscriptions = taskSubscriptions;
        this.preferences = preferences;
        this.users = users;
        this.members = members;
    }

    @Override
    public boolean supports(EmailRecipientStrategy strategy) {
        return SUPPORTED.contains(strategy);
    }

    @Override
    public List<EmailRecipientResolver.RecipientResult> resolveAll(EmailRule rule, Map<String, Object> payload) {
        UUID projectId = asUuid(nested(payload, "project", "id"));
        UUID taskId = asUuid(nested(payload, "task", "id"));
        UUID actorUserId = asUuid(nested(payload, "actor", "userId"));
        boolean excludeActor = rule.recipientConfigJson() != null
                && rule.recipientConfigJson().contains("\"excludeActor\":true");

        LinkedHashSet<UUID> userIds = new LinkedHashSet<>();
        switch (rule.recipientStrategy()) {
            case TASK_ASSIGNEE, TASK_IN_CHARGE -> {
                if (taskId != null) {
                    tasks.findById(taskId).map(Task::inChargeUserId).ifPresent(userIds::add);
                }
                Object assignee = nested(payload, "assignee", "userId");
                UUID assigneeId = asUuid(assignee);
                if (assigneeId != null) userIds.add(assigneeId);
            }
            case TASK_WATCHERS -> {
                if (taskId != null) {
                    taskSubscriptions.findActiveByTaskId(taskId).stream()
                            .map(TaskNotificationSubscription::subscriberUserId)
                            .forEach(userIds::add);
                }
            }
            case PROJECT_WATCHERS, PROJECT_MANAGER -> {
                if (projectId != null) {
                    ProjectSubscriptionType type = rule.recipientStrategy() == EmailRecipientStrategy.PROJECT_MANAGER
                            ? ProjectSubscriptionType.PROJECT_MANAGER
                            : ProjectSubscriptionType.PROJECT_WATCHER;
                    projectSubscriptions.findActiveByProjectIdAndType(projectId, type).stream()
                            .map(ProjectNotificationSubscription::subscriberUserId)
                            .forEach(userIds::add);
                }
            }
            case PROJECT_OWNER -> {
                if (projectId != null) {
                    projects.findById(projectId).map(Project::ownerUserId).ifPresent(userIds::add);
                    projectSubscriptions.findActiveByProjectIdAndType(projectId, ProjectSubscriptionType.PROJECT_OWNER)
                            .stream().map(ProjectNotificationSubscription::subscriberUserId).forEach(userIds::add);
                }
            }
            case CHANGE_WATCHERS -> addWatchers(projectId, ProjectSubscriptionType.CHANGE_WATCHER, userIds);
            case QUOTE_WATCHERS -> addWatchers(projectId, ProjectSubscriptionType.QUOTE_WATCHER, userIds);
            case FINANCE_WATCHERS -> addWatchers(projectId, ProjectSubscriptionType.FINANCE_WATCHER, userIds);
            case BASELINE_WATCHERS -> addWatchers(projectId, ProjectSubscriptionType.BASELINE_WATCHER, userIds);
            case CHANGE_REQUEST_REQUESTER -> {
                UUID requester = asUuid(nested(payload, "requester", "userId"));
                if (requester != null) userIds.add(requester);
            }
            case EVENT_VARIABLE_USER -> {
                UUID userId = asUuid(nested(payload, "targetUser", "userId"));
                if (userId != null) userIds.add(userId);
            }
            default -> { }
        }

        if (excludeActor && actorUserId != null) {
            userIds.remove(actorUserId);
        }

        String eventCode = payload == null ? null : Objects.toString(payload.get("eventCode"), null);
        List<EmailRecipientResolver.RecipientResult> results = new ArrayList<>();
        for (UUID userId : userIds) {
            Optional<IamUser> userOpt = users.findById(userId);
            if (userOpt.isEmpty() || userOpt.get().status() != IamUserStatus.ACTIVE) {
                continue;
            }
            IamUser user = userOpt.get();
            UUID workspaceId = asUuid(nested(payload, "workspace", "id"));
            if (workspaceId != null) {
                var member = members.findByWorkspaceIdAndUserId(workspaceId, userId);
                if (member.isEmpty() || member.get().status() != WorkspaceMemberStatus.ACTIVE) {
                    continue;
                }
            }
            if (!shouldDeliver(projectId, taskId, userId, eventCode, rule.mandatory())) {
                continue;
            }
            results.add(EmailRecipientResolver.RecipientResult.resolved(user.email().value(), user.id()));
        }
        if (results.isEmpty()) {
            return List.of(EmailRecipientResolver.RecipientResult.skipped(
                    "No active recipients for strategy " + rule.recipientStrategy()));
        }
        return results;
    }

    private void addWatchers(UUID projectId, ProjectSubscriptionType type, Set<UUID> userIds) {
        if (projectId == null) return;
        projectSubscriptions.findActiveByProjectIdAndType(projectId, type).stream()
                .map(ProjectNotificationSubscription::subscriberUserId)
                .forEach(userIds::add);
    }

    private boolean shouldDeliver(UUID projectId, UUID taskId, UUID userId, String eventCode, boolean mandatory) {
        if (mandatory) return true;
        if (projectId == null) return true;
        Optional<ProjectNotificationPreference> pref = preferences.findMatching(
                projectId, taskId, userId, eventCode, PreferenceChannel.EMAIL);
        if (pref.isEmpty() && eventCode != null) {
            pref = preferences.findMatching(projectId, taskId, userId, null, PreferenceChannel.EMAIL);
        }
        return pref.map(p -> p.shouldDeliver(false)).orElse(true);
    }

    @SuppressWarnings("unchecked")
    private static Object nested(Map<String, Object> payload, String parent, String child) {
        if (payload == null) return null;
        Object val = payload.get(parent);
        if (val instanceof Map<?, ?> map) {
            return map.get(child);
        }
        // also support flat keys
        return payload.get(parent + "." + child);
    }

    private static UUID asUuid(Object value) {
        if (value == null) return null;
        if (value instanceof UUID uuid) return uuid;
        if (value instanceof String s && !s.isBlank()) {
            try { return UUID.fromString(s.trim()); } catch (IllegalArgumentException e) { return null; }
        }
        return null;
    }
}
