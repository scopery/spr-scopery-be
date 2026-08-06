package com.company.scopery.modules.specpack.block.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataSpecPackBlockRevisionJpaRepository extends JpaRepository<SpecPackBlockRevisionJpaEntity, UUID> {

    @Query("SELECT r FROM SpecPackBlockRevisionJpaEntity r WHERE r.specPackBlockId = :blockId ORDER BY r.revisionNumber DESC")
    List<SpecPackBlockRevisionJpaEntity> findByBlockId(@Param("blockId") UUID blockId);

    @Query("SELECT r FROM SpecPackBlockRevisionJpaEntity r WHERE r.specPackBlockId = :blockId AND r.revisionNumber = :revisionNumber")
    Optional<SpecPackBlockRevisionJpaEntity> findByBlockIdAndRevisionNumber(@Param("blockId") UUID blockId, @Param("revisionNumber") int revisionNumber);
}
