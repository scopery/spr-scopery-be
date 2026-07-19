package com.company.scopery.modules.raid.raidlink.infrastructure.persistence;

import com.company.scopery.modules.raid.raidlink.domain.model.RaidLink;
import com.company.scopery.modules.raid.raidlink.domain.model.RaidLinkRepository;
import com.company.scopery.modules.raid.raidlink.infrastructure.mapper.RaidLinkPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRaidLinkRepository implements RaidLinkRepository {
    private final SpringDataRaidLinkJpaRepository springData;
    private final RaidLinkPersistenceMapper mapper;

    public JpaRaidLinkRepository(SpringDataRaidLinkJpaRepository springData, RaidLinkPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public RaidLink save(RaidLink link) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(link)));
    }

    @Override
    public Optional<RaidLink> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }

    @Override
    public List<RaidLink> findByRaidItemId(UUID raidItemId) {
        return springData.findByRaidItemIdOrderByCreatedAtAsc(raidItemId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteByIdAndProjectId(UUID id, UUID projectId) {
        springData.deleteByIdAndProjectId(id, projectId);
    }
}
