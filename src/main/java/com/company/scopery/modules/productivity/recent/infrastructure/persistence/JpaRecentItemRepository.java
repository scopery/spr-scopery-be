package com.company.scopery.modules.productivity.recent.infrastructure.persistence;
import com.company.scopery.modules.productivity.recent.domain.model.*;
import com.company.scopery.modules.productivity.recent.infrastructure.mapper.RecentItemPersistenceMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaRecentItemRepository implements RecentItemRepository {
    private final SpringDataRecentItemJpaRepository springData; private final RecentItemPersistenceMapper mapper;
    public JpaRecentItemRepository(SpringDataRecentItemJpaRepository springData, RecentItemPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public RecentItem save(RecentItem r) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(r))); }
    @Override public List<RecentItem> findRecentByUser(UUID userId, int limit) {
        return springData.findByUserIdOrderByViewedAtDesc(userId, PageRequest.of(0, limit)).stream().map(mapper::toDomain).toList();
    }
}
