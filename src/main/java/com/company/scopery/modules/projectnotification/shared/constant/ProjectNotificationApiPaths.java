package com.company.scopery.modules.projectnotification.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class ProjectNotificationApiPaths {
    private static final String PROJECTS = ApiPaths.BASE_PATH + "/projects";

    public static final String PROJECT_SUBSCRIPTIONS = PROJECTS + "/{projectId}/notification-subscriptions";
    public static final String TASK_SUBSCRIPTIONS = PROJECTS + "/{projectId}/tasks/{taskId}/notification-subscriptions";
    public static final String PROJECT_PREFERENCES = PROJECTS + "/{projectId}/notification-preferences/me";
    public static final String TASK_PREFERENCES = PROJECTS + "/{projectId}/tasks/{taskId}/notification-preferences/me";
    public static final String REMINDERS = PROJECTS + "/notifications/reminders";

    private ProjectNotificationApiPaths() {}
}
