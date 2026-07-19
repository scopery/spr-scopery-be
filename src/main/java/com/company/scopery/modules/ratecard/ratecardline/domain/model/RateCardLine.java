package com.company.scopery.modules.ratecard.ratecardline.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RateCardLine(
        UUID id, UUID rateCardVersionId, UUID costRoleId, String seniorityLevel, String locationCode,
        String currencyCode, BigDecimal costRatePerHour, BigDecimal billingRatePerHour, String notes,
        int version, Instant createdAt, Instant updatedAt
) {
    public static RateCardLine create(UUID versionId, UUID costRoleId, String seniorityLevel, String locationCode,
                                      String currencyCode, BigDecimal costRatePerHour,
                                      BigDecimal billingRatePerHour, String notes) {
        return new RateCardLine(UUID.randomUUID(), versionId, costRoleId, seniorityLevel, locationCode,
                currencyCode, costRatePerHour, billingRatePerHour, notes, 0, null, null);
    }

    public RateCardLine update(UUID costRoleId, String seniorityLevel, String locationCode, String currencyCode,
                               BigDecimal costRatePerHour, BigDecimal billingRatePerHour, String notes) {
        return new RateCardLine(id, rateCardVersionId, costRoleId, seniorityLevel, locationCode,
                currencyCode, costRatePerHour, billingRatePerHour, notes, version, createdAt, updatedAt);
    }
}
