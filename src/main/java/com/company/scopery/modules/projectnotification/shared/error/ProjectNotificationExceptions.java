package com.company.scopery.modules.projectnotification.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.UUID;

public final class ProjectNotificationExceptions {
    private ProjectNotificationExceptions() {}

    public static AppException subscriptionNotFound(UUID id) {
        return new AppException(ProjectNotificationErrorCatalog.PROJECT_NOTIFICATION_SUBSCRIPTION_NOT_FOUND,
                "Project notification subscription not found: " + id);
    }

    public static AppException subscriptionDuplicate() {
        return new AppException(ProjectNotificationErrorCatalog.PROJECT_NOTIFICATION_SUBSCRIPTION_DUPLICATE);
    }

    public static AppException subscriberNotMember() {
        return new AppException(ProjectNotificationErrorCatalog.PROJECT_NOTIFICATION_SUBSCRIBER_NOT_MEMBER);
    }

    public static AppException subscriberInactive() {
        return new AppException(ProjectNotificationErrorCatalog.PROJECT_NOTIFICATION_SUBSCRIBER_INACTIVE);
    }

    public static AppException subscriberNoAccess() {
        return new AppException(ProjectNotificationErrorCatalog.PROJECT_NOTIFICATION_SUBSCRIBER_NO_ACCESS);
    }

    public static AppException subscriptionMandatory() {
        return new AppException(ProjectNotificationErrorCatalog.PROJECT_NOTIFICATION_SUBSCRIPTION_MANDATORY);
    }

    public static AppException subscriptionAccessDenied() {
        return new AppException(ProjectNotificationErrorCatalog.PROJECT_NOTIFICATION_SUBSCRIPTION_ACCESS_DENIED);
    }

    public static AppException taskSubscriptionNotFound(UUID id) {
        return new AppException(ProjectNotificationErrorCatalog.TASK_NOTIFICATION_SUBSCRIPTION_NOT_FOUND,
                "Task notification subscription not found: " + id);
    }

    public static AppException taskSubscriptionDuplicate() {
        return new AppException(ProjectNotificationErrorCatalog.TASK_NOTIFICATION_SUBSCRIPTION_DUPLICATE);
    }

    public static AppException taskMismatch() {
        return new AppException(ProjectNotificationErrorCatalog.TASK_NOTIFICATION_TASK_MISMATCH);
    }

    public static AppException taskSubscriberNoAccess() {
        return new AppException(ProjectNotificationErrorCatalog.TASK_NOTIFICATION_SUBSCRIBER_NO_ACCESS);
    }

    public static AppException preferenceAccessDenied() {
        return new AppException(ProjectNotificationErrorCatalog.PROJECT_NOTIFICATION_PREFERENCE_ACCESS_DENIED);
    }

    public static AppException preferenceInvalidChannel() {
        return new AppException(ProjectNotificationErrorCatalog.PROJECT_NOTIFICATION_PREFERENCE_INVALID_CHANNEL);
    }

    public static AppException reminderRunNotFound(UUID id) {
        return new AppException(ProjectNotificationErrorCatalog.PROJECT_REMINDER_RUN_NOT_FOUND,
                "Reminder run not found: " + id);
    }

    public static AppException reminderAlreadyRunning() {
        return new AppException(ProjectNotificationErrorCatalog.PROJECT_REMINDER_RUN_ALREADY_RUNNING);
    }

    public static AppException accessDenied() {
        return new AppException(ProjectNotificationErrorCatalog.PROJECT_NOTIFICATION_ACCESS_DENIED);
    }
}
