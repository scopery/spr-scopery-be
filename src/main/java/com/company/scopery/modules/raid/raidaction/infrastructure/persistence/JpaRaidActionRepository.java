package com.company.scopery.modules.raid.raidaction.infrastructure.persistence;
import com.company.scopery.modules.raid.raidaction.domain.model.RaidAction;
import com.company.scopery.modules.raid.raidaction.domain.model.RaidActionRepository;
import com.company.scopery.modules.raid.raidaction.infrastructure.mapper.RaidActionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaRaidActionRepository implements RaidActionRepository {
    private final SpringDataRaidActionJpaRepository springData; private final RaidActionPersistenceMapper mapper;
    public JpaRaidActionRepository(SpringDataRaidActionJpaRepository springData, RaidActionPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public RaidAction save(RaidAction a){ return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(a))); }
    @Override public Optional<RaidAction> findByIdAndProjectId(UUID id, UUID projectId){ return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public List<RaidAction> findByRaidItemId(UUID raidItemId){ return springData.findByRaidItemIdOrderByCreatedAtAsc(raidItemId).stream().map(mapper::toDomain).toList(); }
}
