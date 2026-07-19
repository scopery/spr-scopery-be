package com.company.scopery.modules.projectnotification.tasksubscription.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.projectnotification.shared.activity.ProjectNotificationActivityLogger;
import com.company.scopery.modules.projectnotification.shared.authorization.ProjectNotificationAuthorizationService;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationActivityActions;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationEntityTypes;
import com.company.scopery.modules.projectnotification.shared.error.ProjectNotificationExceptions;
import com.company.scopery.modules.projectnotification.shared.util.ProjectNotificationEnumParser;
import com.company.scopery.modules.projectnotification.tasksubscription.application.command.CreateTaskSubscriptionCommand;
import com.company.scopery.modules.projectnotification.tasksubscription.application.response.TaskNotificationSubscriptionResponse;
import com.company.scopery.modules.projectnotification.tasksubscription.domain.enums.TaskSubscriptionType;
import com.company.scopery.modules.projectnotification.tasksubscription.domain.model.TaskNotificationSubscription;
import com.company.scopery.modules.projectnotification.tasksubscription.domain.model.TaskNotificationSubscriptionRepository;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreateTaskSubscriptionAction {
    private final ProjectRepository projects;
    private final TaskRepository tasks;
    private final TaskNotificationSubscriptionRepository subscriptions;
    private final WorkspaceMemberRepository members;
    private final ProjectNotificationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectNotificationActivityLogger activityLogger;

    public CreateTaskSubscriptionAction(
            ProjectRepository projects, TaskRepository tasks,
            TaskNotificationSubscriptionRepository subscriptions,
            WorkspaceMemberRepository members,
            ProjectNotificationAuthorizationService authorization,
            CurrentUserAuthorizationService currentUser,
            ProjectNotificationActivityLogger activityLogger) {
        this.projects = projects;
        this.tasks = tasks;
        this.subscriptions = subscriptions;
        this.members = members;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public TaskNotificationSubscriptionResponse execute(CreateTaskSubscriptionCommand cmd) {
        IamUser actor = currentUser.resolveCurrentUser();
        UUID subscriberUserId = cmd.subscriberUserId() == null ? actor.id() : cmd.subscriberUserId();
        if (subscriberUserId.equals(actor.id())) {
            authorization.requireTaskSubscribeSelf(cmd.projectId());
        } else {
            authorization.requireTaskManage(cmd.projectId());
        }
        authorization.requireTaskViewAccess(cmd.projectId());

        Project project = projects.findById(cmd.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(cmd.projectId()));
        Task task = tasks.findById(cmd.taskId())
                .orElseThrow(() -> ProjectExceptions.taskNotFound(cmd.taskId()));
        if (!task.projectId().equals(project.id())) {
            throw ProjectNotificationExceptions.taskMismatch();
        }
        TaskSubscriptionType type = ProjectNotificationEnumParser.parseRequired(
                TaskSubscriptionType.class, cmd.subscriptionType(),
                "PROJECT_NOTIFICATION_PREFERENCE_INVALID_EVENT", "subscriptionType");

        WorkspaceMember member = members.findByWorkspaceIdAndUserId(project.workspaceId(), subscriberUserId)
                .orElseThrow(ProjectNotificationExceptions::subscriberNotMember);
        if (member.status() != WorkspaceMemberStatus.ACTIVE) {
            throw ProjectNotificationExceptions.subscriberInactive();
        }
        if (subscriptions.existsActive(task.id(), subscriberUserId, type)) {
            throw ProjectNotificationExceptions.taskSubscriptionDuplicate();
        }

        TaskNotificationSubscription sub = TaskNotificationSubscription.create(
                project.id(), task.id(), project.workspaceId(), subscriberUserId, member.id(), type, false);
        sub = subscriptions.save(sub);
        activityLogger.logSuccess(ProjectNotificationEntityTypes.TASK_SUBSCRIPTION, sub.id(),
                ProjectNotificationActivityActions.SUBSCRIBE_TASK, "Subscribed " + type.name());
        return TaskNotificationSubscriptionResponse.from(sub);
    }
}
