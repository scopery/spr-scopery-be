package com.company.scopery.modules.raid.raidaction.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface RaidActionRepository {
    RaidAction save(RaidAction action);
    Optional<RaidAction> findByIdAndProjectId(UUID id, UUID projectId);
    List<RaidAction> findByRaidItemId(UUID raidItemId);
}
