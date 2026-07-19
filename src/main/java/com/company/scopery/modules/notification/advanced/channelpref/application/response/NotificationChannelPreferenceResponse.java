package com.company.scopery.modules.notification.advanced.channelpref.application.response;
import com.company.scopery.modules.notification.advanced.channelpref.domain.model.NotificationChannelPreference;
import java.util.UUID;
public record NotificationChannelPreferenceResponse(UUID id, String categoryCode, String channelCode, boolean enabled) {
    public static NotificationChannelPreferenceResponse from(NotificationChannelPreference p) {
        return new NotificationChannelPreferenceResponse(p.id(), p.categoryCode(), p.channelCode(), p.enabled());
    }
}
