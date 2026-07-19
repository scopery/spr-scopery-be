package com.company.scopery.modules.productivity.workinbox.infrastructure.persistence;
import com.company.scopery.modules.productivity.workinbox.domain.model.*;
import com.company.scopery.modules.productivity.workinbox.infrastructure.mapper.WorkInboxItemPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaWorkInboxItemRepository implements WorkInboxItemRepository {
    private final SpringDataWorkInboxItemJpaRepository springData; private final WorkInboxItemPersistenceMapper mapper;
    public JpaWorkInboxItemRepository(SpringDataWorkInboxItemJpaRepository springData, WorkInboxItemPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public WorkInboxItem save(WorkInboxItem item) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(item))); }
    @Override public Optional<WorkInboxItem> findByIdAndWorkspaceId(UUID id, UUID workspaceId) { return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain); }
    @Override public List<WorkInboxItem> findActiveByWorkspaceAndUser(UUID workspaceId, UUID userId) {
        return springData.findByWorkspaceIdAndUserIdAndStatusInOrderByDueAtAsc(workspaceId, userId, List.of("ACTIVE","READ","SNOOZED")).stream().map(mapper::toDomain).toList();
    }
}
