package com.company.scopery.modules.projectfinance.vendorcost.domain.model;

import com.company.scopery.modules.projectfinance.vendorcost.domain.enums.VendorCostStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProjectVendorCost(
        UUID id,
        UUID financeScenarioId,
        UUID projectId,
        UUID projectPhaseId,
        String vendorName,
        UUID externalPartyId,
        String description,
        BigDecimal amount,
        String currencyCode,
        VendorCostStatus status,
        Instant archivedAt,
        UUID archivedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectVendorCost create(
            UUID financeScenarioId,
            UUID projectId,
            UUID projectPhaseId,
            String vendorName,
            String description,
            BigDecimal amount,
            String currencyCode) {
        return new ProjectVendorCost(
                UUID.randomUUID(), financeScenarioId, projectId, projectPhaseId, vendorName, null,
                description, amount, currencyCode, VendorCostStatus.ACTIVE,
                null, null, 0, null, null);
    }

    public ProjectVendorCost update(
            UUID projectPhaseId,
            String vendorName,
            String description,
            BigDecimal amount,
            String currencyCode) {
        return new ProjectVendorCost(
                id, financeScenarioId, projectId, projectPhaseId, vendorName, externalPartyId,
                description, amount, currencyCode, status, archivedAt, archivedBy, version,
                createdAt, updatedAt);
    }

    public ProjectVendorCost archive(UUID actorId) {
        return new ProjectVendorCost(
                id, financeScenarioId, projectId, projectPhaseId, vendorName, externalPartyId,
                description, amount, currencyCode, VendorCostStatus.ARCHIVED, Instant.now(), actorId,
                version, createdAt, updatedAt);
    }

    public boolean isActive() {
        return status == VendorCostStatus.ACTIVE;
    }
}
