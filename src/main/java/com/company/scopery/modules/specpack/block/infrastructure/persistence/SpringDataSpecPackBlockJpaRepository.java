package com.company.scopery.modules.specpack.block.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataSpecPackBlockJpaRepository extends JpaRepository<SpecPackBlockJpaEntity, UUID> {

    @Query("SELECT b FROM SpecPackBlockJpaEntity b WHERE b.specPackId = :packId AND b.blockKey = :blockKey AND b.deletedAt IS NULL")
    Optional<SpecPackBlockJpaEntity> findByPackIdAndBlockKey(@Param("packId") UUID packId, @Param("blockKey") String blockKey);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END FROM SpecPackBlockJpaEntity b WHERE b.specPackId = :packId AND b.blockKey = :blockKey AND b.deletedAt IS NULL")
    boolean existsByPackIdAndBlockKey(@Param("packId") UUID packId, @Param("blockKey") String blockKey);

    @Query("SELECT b FROM SpecPackBlockJpaEntity b WHERE b.specPackId = :packId AND b.deletedAt IS NULL ORDER BY b.displayOrder ASC, b.createdAt ASC")
    List<SpecPackBlockJpaEntity> findAllByPackIdOrdered(@Param("packId") UUID packId);
}
