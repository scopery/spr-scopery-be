package com.company.scopery.modules.notification.advanced.preference.infrastructure.mapper;
import com.company.scopery.modules.notification.advanced.preference.domain.model.NotificationPreferenceProfile;
import com.company.scopery.modules.notification.advanced.preference.infrastructure.persistence.NotificationPreferenceProfileJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class NotificationPreferenceProfilePersistenceMapper {
    public NotificationPreferenceProfile toDomain(NotificationPreferenceProfileJpaEntity e) {
        return new NotificationPreferenceProfile(e.getId(), e.getWorkspaceId(), e.getUserId(), e.getTimezone(), e.getDefaultMode(),
                e.isDigestEnabled(), e.isQuietHoursEnabled(), e.getQuietHoursStart(), e.getQuietHoursEnd(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public NotificationPreferenceProfileJpaEntity toJpaEntity(NotificationPreferenceProfile d) {
        NotificationPreferenceProfileJpaEntity e = new NotificationPreferenceProfileJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setUserId(d.userId()); e.setTimezone(d.timezone());
        e.setDefaultMode(d.defaultMode()); e.setDigestEnabled(d.digestEnabled()); e.setQuietHoursEnabled(d.quietHoursEnabled());
        e.setQuietHoursStart(d.quietHoursStart()); e.setQuietHoursEnd(d.quietHoursEnd()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
