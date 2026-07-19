package com.company.scopery.modules.raid.raiditem.infrastructure.persistence;
import com.company.scopery.modules.raid.raiditem.domain.enums.RaidItemType;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItem;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.raiditem.infrastructure.mapper.RaidItemPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaRaidItemRepository implements RaidItemRepository {
    private final SpringDataRaidItemJpaRepository springData; private final RaidItemPersistenceMapper mapper;
    public JpaRaidItemRepository(SpringDataRaidItemJpaRepository springData, RaidItemPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public RaidItem save(RaidItem item){ return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(item))); }
    @Override public Optional<RaidItem> findByIdAndProjectId(UUID id, UUID projectId){ return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public List<RaidItem> findByProjectId(UUID projectId){ return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList(); }
    @Override public List<RaidItem> findByProjectIdAndType(UUID projectId, RaidItemType type){
        return springData.findByProjectIdAndTypeOrderByCreatedAtDesc(projectId, type.name()).stream().map(mapper::toDomain).toList();
    }
}
