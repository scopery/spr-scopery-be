package com.company.scopery.modules.notification.advanced.suppression.infrastructure.persistence;
import com.company.scopery.modules.notification.advanced.suppression.domain.model.*;
import com.company.scopery.modules.notification.advanced.suppression.infrastructure.mapper.NotificationSuppressionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaNotificationSuppressionRepository implements NotificationSuppressionRepository {
    private final SpringDataNotificationSuppressionJpaRepository springData;
    private final NotificationSuppressionPersistenceMapper mapper;
    public JpaNotificationSuppressionRepository(SpringDataNotificationSuppressionJpaRepository springData,
                                                NotificationSuppressionPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public NotificationSuppressionEntry save(NotificationSuppressionEntry e) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e)));
    }
    @Override public List<NotificationSuppressionEntry> findByWorkspaceId(UUID workspaceId) {
        return springData.findByWorkspaceIdOrderBySuppressedAtDesc(workspaceId).stream().map(mapper::toDomain).toList();
    }
}
