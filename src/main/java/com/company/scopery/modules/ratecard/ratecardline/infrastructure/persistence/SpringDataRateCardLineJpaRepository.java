package com.company.scopery.modules.ratecard.ratecardline.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List; import java.util.UUID;

public interface SpringDataRateCardLineJpaRepository extends JpaRepository<RateCardLineJpaEntity, UUID> {
    List<RateCardLineJpaEntity> findAllByRateCardVersionId(UUID versionId);

    @Query("""
            SELECT COUNT(e) > 0 FROM RateCardLineJpaEntity e
            WHERE e.rateCardVersionId = :versionId AND e.costRoleId = :costRoleId
              AND e.currencyCode = :currencyCode
              AND ((:seniorityLevel IS NULL AND e.seniorityLevel IS NULL) OR e.seniorityLevel = :seniorityLevel)
              AND ((:locationCode IS NULL AND e.locationCode IS NULL) OR e.locationCode = :locationCode)
              AND (:excludeLineId IS NULL OR e.id <> :excludeLineId)
            """)
    boolean existsDuplicate(@Param("versionId") UUID versionId, @Param("costRoleId") UUID costRoleId,
                            @Param("seniorityLevel") String seniorityLevel, @Param("locationCode") String locationCode,
                            @Param("currencyCode") String currencyCode, @Param("excludeLineId") UUID excludeLineId);
}
