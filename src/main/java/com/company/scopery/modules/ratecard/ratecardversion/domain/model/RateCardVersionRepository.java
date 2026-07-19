package com.company.scopery.modules.ratecard.ratecardversion.domain.model;

import com.company.scopery.modules.ratecard.ratecardversion.domain.enums.RateCardVersionStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RateCardVersionRepository {
    RateCardVersion save(RateCardVersion version);
    Optional<RateCardVersion> findById(UUID id);
    List<RateCardVersion> findByRateCardId(UUID rateCardId);
    Optional<Integer> findMaxVersionNumber(UUID rateCardId);
    List<RateCardVersion> findPublishedByRateCardId(UUID rateCardId);
    List<RateCardVersion> findPublishedCoveringDate(UUID rateCardId, LocalDate targetDate);
    boolean existsPublishedOverlap(UUID rateCardId, LocalDate from, LocalDate to, UUID excludeVersionId);
}
