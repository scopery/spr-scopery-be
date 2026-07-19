package com.company.scopery.modules.notification.advanced.reminder.infrastructure.persistence;
import com.company.scopery.modules.notification.advanced.reminder.domain.model.*;
import com.company.scopery.modules.notification.advanced.reminder.infrastructure.mapper.ReminderInstancePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.time.Instant; import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaReminderInstanceRepository implements ReminderInstanceRepository {
    private final SpringDataReminderInstanceJpaRepository springData; private final ReminderInstancePersistenceMapper mapper;
    public JpaReminderInstanceRepository(SpringDataReminderInstanceJpaRepository springData, ReminderInstancePersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public ReminderInstance save(ReminderInstance r) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(r))); }
    @Override public Optional<ReminderInstance> findByIdAndWorkspaceId(UUID id, UUID workspaceId) { return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain); }
    @Override public List<ReminderInstance> findByWorkspaceIdAndRecipientUserId(UUID workspaceId, UUID recipientUserId) { return springData.findByWorkspaceIdAndRecipientUserIdOrderByRemindAtAsc(workspaceId, recipientUserId).stream().map(mapper::toDomain).toList(); }
    @Override public List<ReminderInstance> findPendingDue(Instant now) { return springData.findPendingDue(now).stream().map(mapper::toDomain).toList(); }
}
