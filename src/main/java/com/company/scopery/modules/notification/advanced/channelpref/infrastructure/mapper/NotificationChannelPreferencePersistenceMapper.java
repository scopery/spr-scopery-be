package com.company.scopery.modules.notification.advanced.channelpref.infrastructure.mapper;
import com.company.scopery.modules.notification.advanced.channelpref.domain.model.NotificationChannelPreference;
import com.company.scopery.modules.notification.advanced.channelpref.infrastructure.persistence.NotificationChannelPreferenceJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class NotificationChannelPreferencePersistenceMapper {
    public NotificationChannelPreference toDomain(NotificationChannelPreferenceJpaEntity e) {
        return new NotificationChannelPreference(e.getId(), e.getWorkspaceId(), e.getUserId(), e.getCategoryCode(), e.getChannelCode(),
                e.isEnabled(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public NotificationChannelPreferenceJpaEntity toJpaEntity(NotificationChannelPreference d) {
        var e = new NotificationChannelPreferenceJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setUserId(d.userId());
        e.setCategoryCode(d.categoryCode()); e.setChannelCode(d.channelCode()); e.setEnabled(d.enabled());
        e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
