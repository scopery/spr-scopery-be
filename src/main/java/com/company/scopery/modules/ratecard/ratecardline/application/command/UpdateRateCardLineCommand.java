package com.company.scopery.modules.ratecard.ratecardline.application.command;
import java.math.BigDecimal; import java.util.UUID;
public record UpdateRateCardLineCommand(UUID rateCardId, UUID versionId, UUID lineId, UUID costRoleId,
        String seniorityLevel, String locationCode, String currencyCode, BigDecimal costRatePerHour,
        BigDecimal billingRatePerHour, String notes) {}
