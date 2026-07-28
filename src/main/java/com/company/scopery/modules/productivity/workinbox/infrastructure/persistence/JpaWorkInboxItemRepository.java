package com.company.scopery.modules.productivity.workinbox.infrastructure.persistence;

import com.company.scopery.modules.productivity.workinbox.domain.model.WorkInboxItem;
import com.company.scopery.modules.productivity.workinbox.domain.model.WorkInboxItemRepository;
import com.company.scopery.modules.productivity.workinbox.infrastructure.mapper.WorkInboxItemPersistenceMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaWorkInboxItemRepository implements WorkInboxItemRepository {

    private final SpringDataWorkInboxItemJpaRepository springData;
    private final WorkInboxItemPersistenceMapper mapper;

    @PersistenceContext
    private EntityManager em;

    public JpaWorkInboxItemRepository(SpringDataWorkInboxItemJpaRepository springData,
                                      WorkInboxItemPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public WorkInboxItem save(WorkInboxItem item) {
        WorkInboxItemJpaEntity entity = mapper.toJpaEntity(item);
        // New domain items leave createdAt null. Force persist() — Spring Data merge()+@Version
        // otherwise throws ObjectOptimisticLockingFailureException (mapped to 409 RESOURCE_CONFLICT).
        if (entity.getCreatedAt() == null || !springData.existsById(entity.getId())) {
            if (entity.getVersion() != null && entity.getCreatedAt() == null) {
                entity.setVersion(null);
            }
            em.persist(entity);
            em.flush();
            return mapper.toDomain(entity);
        }
        return mapper.toDomain(springData.saveAndFlush(entity));
    }

    @Override
    public Optional<WorkInboxItem> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }

    @Override
    public Optional<WorkInboxItem> findByIdAndUserId(UUID id, UUID userId) {
        return springData.findByIdAndUserId(id, userId).map(mapper::toDomain);
    }

    @Override
    public List<WorkInboxItem> findActiveByWorkspaceAndUser(UUID workspaceId, UUID userId) {
        return springData
                .findByWorkspaceIdAndUserIdAndStatusInOrderByDueAtAsc(
                        workspaceId, userId, List.of("ACTIVE", "READ", "SNOOZED"))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<WorkInboxItem> findActiveByUser(UUID userId) {
        return springData
                .findByUserIdAndStatusInOrderByDueAtAsc(userId, List.of("ACTIVE", "READ", "SNOOZED"))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<WorkInboxItem> findByUserIdAndSourceTypeAndSourceId(UUID userId, String sourceType, UUID sourceId) {
        return springData.findByUserIdAndSourceTypeAndSourceId(userId, sourceType, sourceId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<WorkInboxItem> findBySourceTypeAndSourceId(String sourceType, UUID sourceId) {
        return springData.findBySourceTypeAndSourceId(sourceType, sourceId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<WorkInboxItem> findBySourceTypeInAndStatusIn(List<String> sourceTypes, List<String> statuses) {
        return springData.findBySourceTypeInAndStatusIn(sourceTypes, statuses)
                .stream().map(mapper::toDomain).toList();
    }
}
