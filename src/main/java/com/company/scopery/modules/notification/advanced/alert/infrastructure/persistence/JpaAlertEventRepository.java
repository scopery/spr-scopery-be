package com.company.scopery.modules.notification.advanced.alert.infrastructure.persistence;
import com.company.scopery.modules.notification.advanced.alert.domain.model.*;
import com.company.scopery.modules.notification.advanced.alert.infrastructure.mapper.AlertEventPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaAlertEventRepository implements AlertEventRepository {
    private final SpringDataAlertEventJpaRepository springData; private final AlertEventPersistenceMapper mapper;
    public JpaAlertEventRepository(SpringDataAlertEventJpaRepository springData, AlertEventPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public AlertEvent save(AlertEvent e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(e))); }
    @Override public Optional<AlertEvent> findByIdAndWorkspaceId(UUID id, UUID workspaceId) { return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain); }
    @Override public List<AlertEvent> findByWorkspaceId(UUID workspaceId) { return springData.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId).stream().map(mapper::toDomain).toList(); }
}
