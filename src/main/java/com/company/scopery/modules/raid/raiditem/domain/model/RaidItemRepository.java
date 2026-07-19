package com.company.scopery.modules.raid.raiditem.domain.model;
import com.company.scopery.modules.raid.raiditem.domain.enums.RaidItemType;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface RaidItemRepository {
    RaidItem save(RaidItem item);
    Optional<RaidItem> findByIdAndProjectId(UUID id, UUID projectId);
    List<RaidItem> findByProjectId(UUID projectId);
    List<RaidItem> findByProjectIdAndType(UUID projectId, RaidItemType type);
}
