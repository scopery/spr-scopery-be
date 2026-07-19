package com.company.scopery.modules.productivity.favorite.infrastructure.persistence;
import com.company.scopery.modules.productivity.favorite.domain.model.*;
import com.company.scopery.modules.productivity.favorite.infrastructure.mapper.FavoriteItemPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaFavoriteItemRepository implements FavoriteItemRepository {
    private final SpringDataFavoriteItemJpaRepository springData; private final FavoriteItemPersistenceMapper mapper;
    public JpaFavoriteItemRepository(SpringDataFavoriteItemJpaRepository springData, FavoriteItemPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public FavoriteItem save(FavoriteItem item) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(item))); }
    @Override public Optional<FavoriteItem> findByIdAndWorkspaceId(UUID id, UUID workspaceId) { return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain); }
    @Override public List<FavoriteItem> findActiveByWorkspaceAndUser(UUID workspaceId, UUID userId) {
        return springData.findByWorkspaceIdAndUserIdAndArchivedAtIsNullOrderByCreatedAtDesc(workspaceId, userId).stream().map(mapper::toDomain).toList();
    }
}
