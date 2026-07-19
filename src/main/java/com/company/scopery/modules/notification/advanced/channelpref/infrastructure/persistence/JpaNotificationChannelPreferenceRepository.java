package com.company.scopery.modules.notification.advanced.channelpref.infrastructure.persistence;
import com.company.scopery.modules.notification.advanced.channelpref.domain.model.*;
import com.company.scopery.modules.notification.advanced.channelpref.infrastructure.mapper.NotificationChannelPreferencePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaNotificationChannelPreferenceRepository implements NotificationChannelPreferenceRepository {
    private final SpringDataNotificationChannelPreferenceJpaRepository springData;
    private final NotificationChannelPreferencePersistenceMapper mapper;
    public JpaNotificationChannelPreferenceRepository(SpringDataNotificationChannelPreferenceJpaRepository springData,
                                                      NotificationChannelPreferencePersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public NotificationChannelPreference save(NotificationChannelPreference p) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(p)));
    }
    @Override public List<NotificationChannelPreference> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId) {
        return springData.findByWorkspaceIdAndUserIdOrderByCategoryCodeAscChannelCodeAsc(workspaceId, userId).stream().map(mapper::toDomain).toList();
    }
    @Override public Optional<NotificationChannelPreference> findOne(UUID workspaceId, UUID userId, String category, String channel) {
        return springData.findByWorkspaceIdAndUserIdAndCategoryCodeAndChannelCode(workspaceId, userId, category, channel).map(mapper::toDomain);
    }
}
