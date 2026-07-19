package com.company.scopery.modules.notification.advanced.shared.constant;
import com.company.scopery.common.constant.ApiPaths;
public final class AdvancedNotificationApiPaths {
    private static final String WS = ApiPaths.BASE_PATH + "/workspaces/{workspaceId}/notifications";
    public static final String PREFERENCES_ME = WS + "/preferences/me";
    public static final String SUBSCRIPTIONS = WS + "/subscriptions";
    public static final String DIGEST_RULES = WS + "/digest-rules";
    public static final String DIGEST_RUNS = WS + "/digest-runs";
    public static final String REMINDER_RULES = WS + "/reminder-rules";
    public static final String REMINDER_INSTANCES = WS + "/reminder-instances";
    public static final String ALERT_RULES = WS + "/alert-rules";
    public static final String ALERT_EVENTS = WS + "/alert-events";
    public static final String SUPPRESSIONS = WS + "/suppressions";
    public static final String CHANNEL_PREFERENCES = WS + "/channel-preferences/me";
    private AdvancedNotificationApiPaths(){}
}
