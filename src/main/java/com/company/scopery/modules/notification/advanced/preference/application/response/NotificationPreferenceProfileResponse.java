package com.company.scopery.modules.notification.advanced.preference.application.response;
import com.company.scopery.modules.notification.advanced.preference.domain.model.NotificationPreferenceProfile;
public record NotificationPreferenceProfileResponse(String timezone, String defaultMode, boolean digestEnabled, boolean quietHoursEnabled, String quietHoursStart, String quietHoursEnd) {
    public static NotificationPreferenceProfileResponse from(NotificationPreferenceProfile p) {
        return new NotificationPreferenceProfileResponse(p.timezone(), p.defaultMode(), p.digestEnabled(), p.quietHoursEnabled(), p.quietHoursStart(), p.quietHoursEnd());
    }
}
