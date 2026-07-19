package com.company.scopery.modules.ratecard.ratecardversion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate; import java.util.List; import java.util.Optional; import java.util.UUID;

public interface SpringDataRateCardVersionJpaRepository extends JpaRepository<RateCardVersionJpaEntity, UUID> {
    List<RateCardVersionJpaEntity> findAllByRateCardIdOrderByVersionNumberAsc(UUID rateCardId);
    List<RateCardVersionJpaEntity> findAllByRateCardIdAndStatus(UUID rateCardId, String status);

    @Query("SELECT MAX(e.versionNumber) FROM RateCardVersionJpaEntity e WHERE e.rateCardId = :rateCardId")
    Optional<Integer> findMaxVersionNumber(@Param("rateCardId") UUID rateCardId);

    @Query("""
            SELECT e FROM RateCardVersionJpaEntity e
            WHERE e.rateCardId = :rateCardId AND e.status = 'PUBLISHED'
              AND e.effectiveFrom <= :targetDate
              AND (e.effectiveTo IS NULL OR e.effectiveTo >= :targetDate)
            """)
    List<RateCardVersionJpaEntity> findPublishedCoveringDate(@Param("rateCardId") UUID rateCardId,
                                                             @Param("targetDate") LocalDate targetDate);
}
