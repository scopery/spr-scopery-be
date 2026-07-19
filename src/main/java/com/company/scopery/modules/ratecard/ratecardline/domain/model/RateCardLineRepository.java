package com.company.scopery.modules.ratecard.ratecardline.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RateCardLineRepository {
    RateCardLine save(RateCardLine line);
    Optional<RateCardLine> findById(UUID id);
    List<RateCardLine> findByVersionId(UUID versionId);
    boolean existsDuplicate(UUID versionId, UUID costRoleId, String seniorityLevel, String locationCode,
                            String currencyCode, UUID excludeLineId);
    void delete(UUID id);
}
