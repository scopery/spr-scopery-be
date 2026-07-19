package com.company.scopery.modules.productivity.pin.infrastructure.persistence;
import com.company.scopery.modules.productivity.pin.domain.model.*;
import com.company.scopery.modules.productivity.pin.infrastructure.mapper.PinnedItemPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaPinnedItemRepository implements PinnedItemRepository {
    private final SpringDataPinnedItemJpaRepository springData; private final PinnedItemPersistenceMapper mapper;
    public JpaPinnedItemRepository(SpringDataPinnedItemJpaRepository springData, PinnedItemPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public PinnedItem save(PinnedItem p) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(p))); }
    @Override public Optional<PinnedItem> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public List<PinnedItem> findActiveByWorkspaceAndOwner(UUID workspaceId, UUID ownerUserId) {
        return springData.findByWorkspaceIdAndOwnerUserIdAndArchivedAtIsNullOrderBySortOrderAsc(workspaceId, ownerUserId).stream().map(mapper::toDomain).toList();
    }
}
