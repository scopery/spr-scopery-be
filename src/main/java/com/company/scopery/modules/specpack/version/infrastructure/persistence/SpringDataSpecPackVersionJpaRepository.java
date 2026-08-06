package com.company.scopery.modules.specpack.version.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataSpecPackVersionJpaRepository extends JpaRepository<SpecPackVersionJpaEntity, UUID> {

    @Query("SELECT e FROM SpecPackVersionJpaEntity e WHERE e.specPackId = :specPackId ORDER BY e.versionNumber DESC")
    List<SpecPackVersionJpaEntity> findBySpecPackIdOrderByVersionNumberDesc(@Param("specPackId") UUID specPackId);

    @Query("SELECT MAX(e.versionNumber) FROM SpecPackVersionJpaEntity e WHERE e.specPackId = :specPackId")
    Optional<Integer> findMaxVersionNumberBySpecPackId(@Param("specPackId") UUID specPackId);
}
