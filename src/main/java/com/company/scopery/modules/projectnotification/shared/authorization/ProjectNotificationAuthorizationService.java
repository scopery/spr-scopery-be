package com.company.scopery.modules.projectnotification.shared.authorization;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.projectnotification.shared.error.ProjectNotificationExceptions;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProjectNotificationAuthorizationService {
    private final ProjectWorkspaceAuthorizationService projectAuthorization;

    public ProjectNotificationAuthorizationService(ProjectWorkspaceAuthorizationService projectAuthorization) {
        this.projectAuthorization = projectAuthorization;
    }

    public void requireSubscribeSelf(UUID projectId) {
        require(projectId, IamAuthorities.PROJECT_NOTIFICATION_SUBSCRIBE_SELF);
    }

    public void requireViewSubscriptions(UUID projectId) {
        require(projectId, IamAuthorities.PROJECT_NOTIFICATION_VIEW);
    }

    public void requireManageSubscribers(UUID projectId) {
        require(projectId, IamAuthorities.PROJECT_NOTIFICATION_MANAGE_SUBSCRIBERS);
    }

    public void requirePreferenceView(UUID projectId) {
        require(projectId, IamAuthorities.PROJECT_NOTIFICATION_PREFERENCE_VIEW);
    }

    public void requirePreferenceUpdate(UUID projectId) {
        require(projectId, IamAuthorities.PROJECT_NOTIFICATION_PREFERENCE_UPDATE);
    }

    public void requireTaskSubscribeSelf(UUID projectId) {
        require(projectId, IamAuthorities.TASK_NOTIFICATION_SUBSCRIBE_SELF);
    }

    public void requireTaskView(UUID projectId) {
        require(projectId, IamAuthorities.TASK_NOTIFICATION_VIEW);
    }

    public void requireTaskManage(UUID projectId) {
        require(projectId, IamAuthorities.TASK_NOTIFICATION_MANAGE_SUBSCRIBERS);
    }

    public void requireReminderRun(UUID workspaceId) {
        try {
            projectAuthorization.requireWorkspacePermission(workspaceId, IamAuthorities.PROJECT_REMINDER_RUN);
        } catch (RuntimeException ex) {
            throw ProjectNotificationExceptions.accessDenied();
        }
    }

    /** Soft check for project view used at subscription time. */
    public void requireProjectView(UUID projectId) {
        try {
            projectAuthorization.requireProjectView(projectId);
        } catch (RuntimeException ex) {
            throw ProjectNotificationExceptions.subscriberNoAccess();
        }
    }

    public void requireTaskViewAccess(UUID projectId) {
        try {
            projectAuthorization.requireTaskView(projectId);
        } catch (RuntimeException ex) {
            throw ProjectNotificationExceptions.taskSubscriberNoAccess();
        }
    }

    private void require(UUID projectId, IamPermissionAction authority) {
        try {
            projectAuthorization.requireProjectPermission(projectId, authority);
        } catch (RuntimeException ex) {
            throw ProjectNotificationExceptions.accessDenied();
        }
    }
}
