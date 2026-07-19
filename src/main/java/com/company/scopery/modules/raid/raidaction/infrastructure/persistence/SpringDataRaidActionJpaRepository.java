package com.company.scopery.modules.raid.raidaction.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataRaidActionJpaRepository extends JpaRepository<RaidActionJpaEntity, UUID> {
    Optional<RaidActionJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<RaidActionJpaEntity> findByRaidItemIdOrderByCreatedAtAsc(UUID raidItemId);
}
