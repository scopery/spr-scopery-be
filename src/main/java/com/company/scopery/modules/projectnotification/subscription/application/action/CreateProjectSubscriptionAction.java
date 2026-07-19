package com.company.scopery.modules.projectnotification.subscription.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectnotification.shared.activity.ProjectNotificationActivityLogger;
import com.company.scopery.modules.projectnotification.shared.authorization.ProjectNotificationAuthorizationService;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationActivityActions;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationEntityTypes;
import com.company.scopery.modules.projectnotification.shared.error.ProjectNotificationExceptions;
import com.company.scopery.modules.projectnotification.shared.util.ProjectNotificationEnumParser;
import com.company.scopery.modules.projectnotification.subscription.application.command.CreateProjectSubscriptionCommand;
import com.company.scopery.modules.projectnotification.subscription.application.response.ProjectNotificationSubscriptionResponse;
import com.company.scopery.modules.projectnotification.subscription.domain.enums.ProjectSubscriptionType;
import com.company.scopery.modules.projectnotification.subscription.domain.model.ProjectNotificationSubscription;
import com.company.scopery.modules.projectnotification.subscription.domain.model.ProjectNotificationSubscriptionRepository;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreateProjectSubscriptionAction {
    private final ProjectRepository projects;
    private final ProjectNotificationSubscriptionRepository subscriptions;
    private final WorkspaceMemberRepository members;
    private final ProjectNotificationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectNotificationActivityLogger activityLogger;

    public CreateProjectSubscriptionAction(
            ProjectRepository projects,
            ProjectNotificationSubscriptionRepository subscriptions,
            WorkspaceMemberRepository members,
            ProjectNotificationAuthorizationService authorization,
            CurrentUserAuthorizationService currentUser,
            ProjectNotificationActivityLogger activityLogger) {
        this.projects = projects;
        this.subscriptions = subscriptions;
        this.members = members;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProjectNotificationSubscriptionResponse execute(CreateProjectSubscriptionCommand cmd) {
        IamUser actor = currentUser.resolveCurrentUser();
        UUID subscriberUserId = cmd.subscriberUserId() == null ? actor.id() : cmd.subscriberUserId();
        if (subscriberUserId.equals(actor.id())) {
            authorization.requireSubscribeSelf(cmd.projectId());
        } else {
            authorization.requireManageSubscribers(cmd.projectId());
        }
        authorization.requireProjectView(cmd.projectId());

        Project project = projects.findById(cmd.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(cmd.projectId()));
        ProjectSubscriptionType type = ProjectNotificationEnumParser.parseRequired(
                ProjectSubscriptionType.class, cmd.subscriptionType(),
                "PROJECT_NOTIFICATION_PREFERENCE_INVALID_EVENT", "subscriptionType");

        WorkspaceMember member = members.findByWorkspaceIdAndUserId(project.workspaceId(), subscriberUserId)
                .orElseThrow(ProjectNotificationExceptions::subscriberNotMember);
        if (member.status() != WorkspaceMemberStatus.ACTIVE) {
            throw ProjectNotificationExceptions.subscriberInactive();
        }
        if (subscriptions.existsActive(project.id(), subscriberUserId, type)) {
            throw ProjectNotificationExceptions.subscriptionDuplicate();
        }

        ProjectNotificationSubscription sub = ProjectNotificationSubscription.create(
                project.id(), project.workspaceId(), subscriberUserId, member.id(), type, false);
        sub = subscriptions.save(sub);
        activityLogger.logSuccess(ProjectNotificationEntityTypes.PROJECT_SUBSCRIPTION, sub.id(),
                ProjectNotificationActivityActions.SUBSCRIBE_PROJECT, "Subscribed " + type.name());
        return ProjectNotificationSubscriptionResponse.from(sub);
    }
}
