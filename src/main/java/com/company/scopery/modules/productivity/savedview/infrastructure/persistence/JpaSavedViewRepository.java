package com.company.scopery.modules.productivity.savedview.infrastructure.persistence;
import com.company.scopery.modules.productivity.savedview.domain.model.*;
import com.company.scopery.modules.productivity.savedview.infrastructure.mapper.SavedViewPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaSavedViewRepository implements SavedViewRepository {
    private final SpringDataSavedViewJpaRepository springData; private final SavedViewPersistenceMapper mapper;
    public JpaSavedViewRepository(SpringDataSavedViewJpaRepository springData, SavedViewPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public SavedView save(SavedView v) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(v))); }
    @Override public Optional<SavedView> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public List<SavedView> findActiveByWorkspaceAndOwner(UUID workspaceId, UUID ownerUserId) {
        return springData.findByWorkspaceIdAndOwnerUserIdAndStatus(workspaceId, ownerUserId, "ACTIVE").stream().map(mapper::toDomain).toList();
    }
}
