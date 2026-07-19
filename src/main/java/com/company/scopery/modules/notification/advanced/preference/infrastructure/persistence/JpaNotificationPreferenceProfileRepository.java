package com.company.scopery.modules.notification.advanced.preference.infrastructure.persistence;
import com.company.scopery.modules.notification.advanced.preference.domain.model.*;
import com.company.scopery.modules.notification.advanced.preference.infrastructure.mapper.NotificationPreferenceProfilePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaNotificationPreferenceProfileRepository implements NotificationPreferenceProfileRepository {
    private final SpringDataNotificationPreferenceProfileJpaRepository springData; private final NotificationPreferenceProfilePersistenceMapper mapper;
    public JpaNotificationPreferenceProfileRepository(SpringDataNotificationPreferenceProfileJpaRepository springData, NotificationPreferenceProfilePersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public NotificationPreferenceProfile save(NotificationPreferenceProfile p) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(p))); }
    @Override public Optional<NotificationPreferenceProfile> findByWorkspaceAndUser(UUID workspaceId, UUID userId) { return springData.findByWorkspaceIdAndUserId(workspaceId, userId).map(mapper::toDomain); }
}
