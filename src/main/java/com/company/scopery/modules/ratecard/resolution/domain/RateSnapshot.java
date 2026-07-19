package com.company.scopery.modules.ratecard.resolution.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Immutable rate resolution snapshot contract (RTE-009).
 * Never includes salary or payroll fields.
 */
public record RateSnapshot(
        UUID rateCardId,
        UUID rateCardVersionId,
        UUID rateCardLineId,
        UUID costRoleId,
        String costRoleCode,
        BigDecimal baseCostRate,
        BigDecimal adjustedCostRate,
        BigDecimal baseBillingRate,
        BigDecimal adjustedBillingRate,
        String currencyCode,
        UUID inflationPolicyId,
        BigDecimal inflationPercent,
        BigDecimal yearsForward,
        LocalDate resolvedForDate,
        Instant resolvedAt
) {}
