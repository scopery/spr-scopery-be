package com.company.scopery.modules.raid.raidlink.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataRaidLinkJpaRepository extends JpaRepository<RaidLinkJpaEntity, UUID> {
    List<RaidLinkJpaEntity> findByRaidItemIdOrderByCreatedAtAsc(UUID raidItemId);
    Optional<RaidLinkJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    void deleteByIdAndProjectId(UUID id, UUID projectId);
}
