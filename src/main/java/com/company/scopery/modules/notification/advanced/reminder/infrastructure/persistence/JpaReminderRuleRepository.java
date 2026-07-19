package com.company.scopery.modules.notification.advanced.reminder.infrastructure.persistence;
import com.company.scopery.modules.notification.advanced.reminder.domain.model.*;
import com.company.scopery.modules.notification.advanced.reminder.infrastructure.mapper.ReminderRulePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaReminderRuleRepository implements ReminderRuleRepository {
    private final SpringDataReminderRuleJpaRepository springData; private final ReminderRulePersistenceMapper mapper;
    public JpaReminderRuleRepository(SpringDataReminderRuleJpaRepository springData, ReminderRulePersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public ReminderRule save(ReminderRule r) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(r))); }
    @Override public Optional<ReminderRule> findByIdAndWorkspaceId(UUID id, UUID workspaceId) { return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain); }
    @Override public List<ReminderRule> findByWorkspaceId(UUID workspaceId) { return springData.findByWorkspaceIdOrderByRuleCodeAsc(workspaceId).stream().map(mapper::toDomain).toList(); }
    @Override public List<ReminderRule> findActiveByWorkspaceId(UUID workspaceId) { return springData.findByWorkspaceIdAndStatusOrderByRuleCodeAsc(workspaceId, "ACTIVE").stream().map(mapper::toDomain).toList(); }
}
