package com.company.scopery.modules.raid.raiditem.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataRaidItemJpaRepository extends JpaRepository<RaidItemJpaEntity, UUID> {
    Optional<RaidItemJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<RaidItemJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    List<RaidItemJpaEntity> findByProjectIdAndTypeOrderByCreatedAtDesc(UUID projectId, String type);
}
