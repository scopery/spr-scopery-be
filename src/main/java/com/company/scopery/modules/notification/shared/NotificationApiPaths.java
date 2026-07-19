package com.company.scopery.modules.notification.shared;

import com.company.scopery.common.constant.ApiPaths;

public final class NotificationApiPaths {

    private static final String BASE = ApiPaths.BASE_PATH + "/notification";

    public static final String EMAIL_TEMPLATES  = BASE + "/email-templates";
    public static final String EMAIL_RULES      = BASE + "/email-rules";
    public static final String EMAIL_DELIVERIES = BASE + "/email-deliveries";
    public static final String EMAIL_OUTBOX     = BASE + "/email-outbox";
    /** In-app inbox — under /api/notifications (not /api/notification/...). */
    public static final String NOTIFICATIONS    = ApiPaths.BASE_PATH + "/notifications";

    private NotificationApiPaths() {}
}
