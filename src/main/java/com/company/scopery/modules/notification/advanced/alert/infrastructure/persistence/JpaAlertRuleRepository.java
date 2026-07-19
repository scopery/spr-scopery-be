package com.company.scopery.modules.notification.advanced.alert.infrastructure.persistence;
import com.company.scopery.modules.notification.advanced.alert.domain.model.*;
import com.company.scopery.modules.notification.advanced.alert.infrastructure.mapper.AlertRulePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaAlertRuleRepository implements AlertRuleRepository {
    private final SpringDataAlertRuleJpaRepository springData; private final AlertRulePersistenceMapper mapper;
    public JpaAlertRuleRepository(SpringDataAlertRuleJpaRepository springData, AlertRulePersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public AlertRule save(AlertRule r) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(r))); }
    @Override public Optional<AlertRule> findByIdAndWorkspaceId(UUID id, UUID workspaceId) { return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain); }
    @Override public List<AlertRule> findByWorkspaceId(UUID workspaceId) { return springData.findByWorkspaceIdOrderByRuleCodeAsc(workspaceId).stream().map(mapper::toDomain).toList(); }
    @Override public List<AlertRule> findAllActive() { return springData.findByStatus("ACTIVE").stream().map(mapper::toDomain).toList(); }
}
