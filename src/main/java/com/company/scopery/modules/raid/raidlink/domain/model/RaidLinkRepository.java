package com.company.scopery.modules.raid.raidlink.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RaidLinkRepository {
    RaidLink save(RaidLink link);
    Optional<RaidLink> findByIdAndProjectId(UUID id, UUID projectId);
    List<RaidLink> findByRaidItemId(UUID raidItemId);
    void deleteByIdAndProjectId(UUID id, UUID projectId);
}
